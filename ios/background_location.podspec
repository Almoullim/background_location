#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
Pod::Spec.new do |s|
  s.name             = 'background_location'
  s.version          = '0.0.1'
  s.summary          = 'Flutter background location plugin for Android and iOS'
  s.description      = <<-DESC
Flutter background location plugin for Android and iOS
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Flutter background location plugin for Android and iOS' => 'ali.almoullim@gmail.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.swift_version = '4.2'
  s.ios.deployment_target = '10.0'
end

