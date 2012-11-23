# Norwegian phonetic name search in Lucene

[Apache Solr](http://lucene.apache.org/solr/) is a great search platform.
It is based on [Lucene](http://lucene.apache.org/core/), and already does most of what you'll ever need out of the box.
Sometimes, however, you need to get your hands dirty in order to get things Just Right™.

In this blogpost we will show how to implement a custom `TokenFilter` in Lucene, and how this can be used to implement phonetic search on Norwegian names.

*TODO: write everything else.*