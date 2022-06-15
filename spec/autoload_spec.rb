# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

describe "Autoload" do
  before(:all) do
    @origin_level = SgtnClient.logger.level
    SgtnClient.logger.level = Logger::INFO
  end
  after(:all) do
    SgtnClient.logger.level = @origin_level
  end

  it "#should be able to set trace on each methods" do
    prefix = "-----------------------------"
    expect {
      traverse_modules(SgtnClient) do |method|
        # trace = TracePoint.new(:call, :return) do |tp|
        #   if tp.event == :return
        #     SgtnClient.logger.info "#{prefix}#{[tp.defined_class, tp.method_id, tp.event]}"
        #   else
        #     SgtnClient.logger.info "#{prefix}#{[tp.defined_class, tp.method_id, tp.event, extract_arguments(tp)]}"
        #   end
        # end
        # trace.enable(target: method)
      rescue ArgumentError => e
        SgtnClient.logger.error "********************Failed to enable trace on #{method} | #{e.message}"
      else
        SgtnClient.logger.info "#{prefix}Enabled trace on #{method}"
      end
    }.to_not raise_error
  end

  it "#all files are autoloaded" do
    exception_files = Set["sgtn-client/sgtn-client", "sgtn-client/cldr/localized_datetime", "sgtn-client/cldr/localized_date",
                          "sgtn-client/cldr/localized_time", "sgtn-client/cldr/localized_str", "sgtn-client/cldr/core_ext"]
    autoloaded_files = Set[]
    base_path = Pathname.new("lib")
    all_files = base_path.glob("*/**/*.rb").each_with_object(Set[]) do |file, obj|
      obj.add(file.to_s.delete_prefix("lib/").delete_suffix(".rb"))
      contents = File.read(file)
      contents.scan(/^\s*autoload\s+:?(?<const>\w+),\s+(?<quote>['"])(?<path>.+)\k<quote>\s*$/).each do |m|
        raise "add twice. #{m[2]}" unless autoloaded_files.add?(m[2])
        # expect(autoloaded_files.add?(m[2])).to be_truthy
      end
    end
    all_files.each do |file|
      next if exception_files.include?(file)

      raise "Not autoloaded. #{file}" unless autoloaded_files.include?(file)
    end
  end
end
