# Generated by Buildr 1.3.3, change to your liking
# Standard maven2 repository
repositories.remote << 'http://repo2.maven.org/maven2'
repositories.remote << 'http://www.ibiblio.org/maven2'

SERVLET_API = 'javax.servlet:servlet-api:jar:2.5'
SLF4J = transitive( 'org.slf4j:slf4j-simple:jar:1.5.6' )

desc 'servlet-original-transport-support'
define 'servlet-original-transport-support' do
  project.group = 'de.javakaffee.web'
  project.version = '1.2'
  
  compile.with(SERVLET_API, SLF4J).using(:source=>'1.5', :target=>'1.5')
  
  package :jar, :id => 'servlet-original-transport-support'
  package :sources
  package :javadoc

end
