require "rubygems"
require "braintree"
require "json"
require "sinatra/base"

class Application < Sinatra::Base
  Braintree::Configuration.environment = :sandbox
  Braintree::Configuration.merchant_id = "your_merchant_id"
  Braintree::Configuration.public_key  = "your_public_key"
  Braintree::Configuration.private_key = "your_private_key"

  post "/" do
    result = Braintree::Transaction.sale(
      :amount => "100.00",
      :credit_card => {
        :number => params["cc_number"],
        :expiration_date => params["cc_exp_date"],
        :cvv => params["cc_cvv"]
      }
    )

    if result.success?
      response = {
        :success => true,
        :credit_card_number => result.transaction.credit_card_details.masked_number,
        :credit_card_type => result.transaction.credit_card_details.card_type
      }.to_json
    else
      response = {
        :success => false,
        :error_message => result.message
      }.to_json
    end
  end
end
