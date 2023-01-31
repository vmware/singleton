# Copyright 2022-2023 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

# Configure Bundler
require 'bundler/setup'
require './runner'

Bundler.require :default, :sample

class App < Sinatra::Application
  enable :sessions

  get '/' do
    haml :index
  end

  get '/translation/all' do
    @Result = RunSample.run('translation/all.rb', '@result')
    haml :display_hash, locals: {
      header: 'translations',
      display_hash: @Result
    }
  end

  get '/formatting/all' do
    @Result = RunSample.run('formatting/all.rb', '@result')
    haml :display_hash, locals: {
      header: 'Got 1 matching payments',
      display_hash: { "success": true, "result": @Result }
    }
  end

  Dir['translation/*', 'formatting/*'].each do |file_name|
    get "/#{file_name.sub(/rb$/, 'html')}" do
      CodeRay.scan(File.read(file_name), 'ruby').page title: "Source: #{file_name}"
    end
  end
end
