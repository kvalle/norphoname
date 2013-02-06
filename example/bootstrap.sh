#!/bin/bash
set -e

# todo: script som 
# - bygger filter-prosjektet
# - lager lib-mappen og kopierer/symlinker inn jar-fila
# - kjører indexeren

cd ../filter
mvn clean install
mkdir -p ../example/solr-4.1.0/solr/collection1/lib
cd ../example/solr-4.1.0/solr/collection1/lib
ln -s ../../../../../filter/target/navn-på-jar.jar