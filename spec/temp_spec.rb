# require 'sgtn-client/api/translation'
# RSpec.describe Translation do
#   before :all do
#     env = SgtnClient::Config.default_environment
#     SgtnClient::Config.configurations[env]['bundle_mode'] = 'offline'
#     SgtnClient::Source.loadBundles('default')
#   end

#   it 'should return translation' do
#         id = SgtnClient::ItemID.new('JAVA', 'de')
#         item = SgtnClient::BundleData.new(id)

#     trans = Translation.get_cs('JAVA', 'de')
# #     require 'pry-byebug'
# # binding.pry
# # sleep 0
#   end
# end
