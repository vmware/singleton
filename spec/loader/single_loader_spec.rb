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

  include_context 'reset client' do
    before(:all) do
      SgtnClient::Config.configurations[SgtnClient::Config.default_environment] = config
    end
  end

  it '#only one request is made' do
    max_thread_num = 100
    pool = Concurrent::ThreadPoolExecutor.new(min_threads: max_thread_num, max_threads: max_thread_num * 10, max_queue: 0)
    TracePoint.new(:return) do |tp|
      SgtnClient.logger.debug tp.inspect
    end.enable(target: SgtnClient::TranslationLoader::SingleLoader.instance_method(:load_bundle))

    semaphore = Concurrent::Semaphore.new(max_thread_num)
    lock = Concurrent::ReadWriteLock.new
    expect(lock.acquire_write_lock).to be true # make all threads to wait for the lock

    expect(loader).to receive(:super_load).with(component, locale).once do
      SgtnClient.logger.debug 'Start Loading...................'
      sleep 0.01
      SgtnClient.logger.debug 'End Loading...................'
      return_value
    end

    start = Time.now
    (0...max_thread_num).each do |_|
      queued = pool.post(component, locale) do |c, l|
        semaphore.acquire
        lock.with_read_lock { loader.load_bundle(c, l) }
      end
      SgtnClient.logger.debug "queued: #{queued}"
    end
    sleep 0.001 while semaphore.available_permits != 0 # wait for all threads ready to load
    lock.release_write_lock # let all threads to load simultaneously

    pool.shutdown
    pool.wait_for_termination
    expect(pool.shutdown?).to be true

    SgtnClient.logger.info "test time: #{Time.now - start}"
  end

  it '#return correct data' do
    expect(loader).to receive(:super_load).with(component, locale).once.and_return(return_value)

    expect(loader.load_bundle(component, locale)).to be return_value

    id = SgtnClient::Common::BundleID.new(component, locale)
    expect(SgtnClient::TranslationLoader::SingleLoader.instance_variable_get(:@single_loader).remove_object(id)).to be nil
  end
end
