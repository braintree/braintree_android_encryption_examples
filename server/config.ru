require './app'
require 'rack'
require 'rack/ssl'
require 'webrick'
require 'webrick/https'
require 'openssl'

use Rack::SSL

private_key_file = File.expand_path(File.join(File.dirname(__FILE__), "ssl", "privateKey.key"))
cert_file = File.expand_path(File.join(File.dirname(__FILE__), "ssl", "certificate.crt"))

pkey = OpenSSL::PKey::RSA.new(File.read(private_key_file))
cert = OpenSSL::X509::Certificate.new(File.read(cert_file))

port = ENV['SSL_TEST_PORT'] || 8443

options = {
  :Port => port,
  :Logger => WEBrick::Log::new(nil, WEBrick::Log::ERROR),
  :DocumentRoot => File.join(File.dirname(__FILE__)),
  :SSLEnable => true,
  :SSLVerifyClient => OpenSSL::SSL::VERIFY_NONE,
  :SSLCertificate => cert,
  :SSLPrivateKey => pkey,
  :SSLCertName => [ [ "CN",WEBrick::Utils::getservername ] ]
}

server = WEBrick::HTTPServer.new(options)
server.mount "/", Rack::Handler::WEBrick, Application.new
Signal.trap(:INT) { server.shutdown }

puts "=> Booting WEBrick with SSL"
puts "=> Sinatra application starting on https://0.0.0.0:#{port}"
puts "=> Ctrl-C to shutdown server"

server.start
