
#h3 OSGI properties
# configure zip file containing migration scripts, for auto updating database
com.eriklievaart.postgres.migrate.zip=/data/hu/humigrate.zip
# configure script dir for manually running scripts (development)
com.eriklievaart.postgres.script.dir=/home/eazy/Development/git/humigrate/main/static/zip

#h3 connection settings
# configure the database connection settings in the following file:
/data/config/postgres.properties

# the following properties are available (defaults after '=')
host=localhost
database=application
user=postgres
password=Ch@ng3m3

