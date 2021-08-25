# Configure Bundler
require 'bundler/setup'
require './runner.rb'

Bundler.require :default, :sample


class App < Sinatra::Application
  enable :sessions

  get '/' do
    haml :index
  end

  get "/translation/all" do
    @Result  = RunSample.run("translation/all.rb", "@Result")
    haml :display_hash, :locals => {
      :header => "Got 1 matching payments",
      :display_hash => {"success": true, "result": @Result} }
  end

  get "/formatting/all" do
    @Result  = RunSample.run("formatting/all.rb", "@Result")
    haml :display_hash, :locals => {
      :header => "Got 1 matching payments",
      :display_hash => {"success": true, "result": @Result} }
  end


  Dir["translation/*", "formatting/*"].each do |file_name|
    get "/#{file_name.sub(/rb$/, "html")}" do
      CodeRay.scan(File.read(file_name), "ruby").page :title => "Source: #{file_name}"
    end
  end

end
