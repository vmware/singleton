# -*- encoding: utf-8 -*-

require File.expand_path('../lib/version', __FILE__)

Gem::Specification.new do |s|
  s.name = 'singleton-ruby'
  s.version = VERSION
  s.authors = ['VMware G11n Team']
  s.description = 'A Singleton client for Ruby'
  s.license = 'MIT'
  s.email = 'li@vmware.com'
  #s.executables = ['sgtnclient']
  s.extra_rdoc_files = ['README.md']
  #s.files = ["lib/singleton-ruby.rb"]
  #s.test_files = `git ls-files -z spec/`.split("\0")
  s.homepage = 'https://github.com/vmware/singleton'
  s.summary = 'Singleton client for Ruby.'

  s.files         = Dir["{bin,spec,lib}/**/*"] + ["Rakefile", "README.md", "Gemfile"] + Dir["data/*"]
  s.executables   = s.files.grep(%r{^bin/}).map{ |f| File.basename(f) }
  s.test_files    = s.files.grep(%r{^(test|spec|features)/})
  s.require_paths = ["lib"]

  s.add_development_dependency('rest-client', '~> 2.0')
  s.add_development_dependency('multi_json', '~> 1.0')
  
  s.add_development_dependency('webmock', '~> 2.0')
  s.add_development_dependency('rspec', '~> 3.0')
  s.add_development_dependency('pry', '~> 0')
  s.add_development_dependency('pry-doc', '~> 0')
  s.add_development_dependency('rdoc', '>= 2.4.2', '< 6.0')
  s.add_development_dependency('rubocop', '~> 0.49')

  s.add_dependency('http-accept', '>= 1.7.0', '< 2.0')
  s.add_dependency('http-cookie', '>= 1.0.2', '< 2.0')
  s.add_dependency('mime-types', '>= 1.16', '< 4.0')
  s.add_dependency('netrc', '~> 0.8')
  s.add_dependency('rest-client', '~> 2.0')
  s.add_dependency('multi_json', '~> 1.0')
  s.add_dependency('twitter_cldr', '~> 6.6')

  s.required_ruby_version = '>= 2.0.0'

end
