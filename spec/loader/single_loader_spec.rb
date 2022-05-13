#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

require 'sgtn-client/loader/single_loader'
autoload :Concurrent, 'concurrent'

describe SgtnClient::TranslationLoader::SingleLoader, :include_helpers, :extend_helpers do
  loader = Class.new do
    prepend SgtnClient::TranslationLoader::SingleLoader
    def load_bundle(component, locale)
      super_load(component, locale)
    end
  end.new
  return_value = {}

  prefix = '-----------------------------'
  trace = TracePoint.new(:call, :return) do |tp|
    if tp.event == :return
      SgtnClient.logger.debug "#{prefix}#{[tp.method_id, tp.event, tp.return_value]}"
    else
      SgtnClient.logger.debug "#{prefix}#{[tp.method_id, tp.event, tp.binding.local_variables.map { |var| tp.binding.local_variable_get(var) }]}"
    end
  end

  include_context 'reset client' do
    before(:all) do
      SgtnClient::Config.configurations[SgtnClient::Config.default_environment] = config
      trace.enable(target: SgtnClient::TranslationLoader::SingleLoader.instance_method(:load_bundle))
    end
    after(:all) do
      trade.disable
    end
  end

  it '#only one request is made' do
    max_thread_num = 100

    semaphore = Concurrent::Semaphore.new(max_thread_num)
    lock = Concurrent::ReadWriteLock.new
    expect(lock.acquire_write_lock).to be true # make all threads to wait for the lock

    expect(loader).to receive(:super_load).with(component, locale).at_least(:once) do
      SgtnClient.logger.debug 'Start Loading...................'
      sleep 0.001
      SgtnClient.logger.debug 'End Loading...................'

      # return_value
      Thread.current.name
    end

    start = Time.now
    (0...max_thread_num).each do |_|
      Thread.new do
        semaphore.acquire
        lock.with_read_lock { expect(loader.load_bundle(component, locale)).not_to be nil }
      end
    end
    sleep 0.001 while semaphore.available_permits != 0 # wait for all threads ready to load
    lock.release_write_lock # let all threads to load simultaneously

    wait_threads_finish
    SgtnClient.logger.info "test time: #{Time.now - start}"
  end

  it '#return correct data' do
    expect(loader).to receive(:super_load).with(component, locale).once.and_return(return_value)

    expect(loader.load_bundle(component, locale)).to be return_value

    id = SgtnClient::Common::BundleID.new(component, locale)
    expect(SgtnClient::TranslationLoader::SingleLoader.instance_variable_get(:@single_loader).remove_object(id)).to be nil
  end
end
