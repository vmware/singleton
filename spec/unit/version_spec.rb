require 'spec_helper'

describe VERSION do
  describe 'version' do
    # test that there is a sane version number to avoid accidental 0.0.0 again
    it 'has a version > 0.0.0, < 3.0' do
      ver = Gem::Version.new(VERSION)
      expect(Gem::Requirement.new('> 0.0.0', '< 3.0')).to be_satisfied_by(ver)
    end
  end
end
