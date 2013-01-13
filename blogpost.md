# Custom Lucene TokenFilters for Solr

[Apache Solr](http://lucene.apache.org/solr/) is a great search platform.
It is based on [Lucene](http://lucene.apache.org/core/), and already does most of what you'll ever need out of the box.
Sometimes, however, you need to get your hands dirty in order to get things Just Right™.

In this blogpost we will show how to implement a custom `TokenFilter` in Lucene.
This can be used for a number of things, but our specific example will be how to implement a custom phonetic search.

This post won't be a complete beginners introduction to Solr or Lucene, so some familiarity is expected.
However, you shouldn't need to know much more than you would from playing with the [Solr documentation example](http://lucene.apache.org/solr/api-4_0_0-BETA/doc-files/tutorial.html).


## Use Case: Phonetic Search

A phonetic algorithm is a way to represent take a word as input, and convert this to a digest representing the way the word sounds when spoken.
Words sounding roughly the same should thus be represented the same way by the algorithm, and when searching words sounding like the query should match its phonetic digest.

Solr already supports four different phonetic algorithms: [Soundex](http://en.wikipedia.org/wiki/Soundex), RefinedSoundex, [Metaphone](http://en.wikipedia.org/wiki/Metaphone) and DoubleMetaphone.
For many practical purposes, one of these four will suffice.
In some situations, such as when searching for norwegian names, these four just dosn't cut it.

In order to use a custom phonetic algorithm, we will implement a TokenFilters for this purpose.


## The Fields and Field Types

The documents and their fields, as well as the field types, are defined in our [schema.xml](https://github.com/kvalle/norphoname/blob/master/schema.xml) file.

In our example, the documents represent persons, with names and birth dates:

	<fields>
		<field name="id" type="string" indexed="true" stored="true" />
		<field name="name" type="text_general" indexed="true" stored="true" />
		<field name="name_phonetic" type="phonetic" indexed="true" stored="false" />
		<field name="birth_date" type="string" indexed="true" stored="false" />
	</fields>

The field called `name_phonetic` is where we'll store our phonetic encoding of the names.
This field does not need to be specified when documents are inserted into the collection, but will be copied and transformed from the `name` field automatically.

	<copyField source="name" dest="name_phonetic" />

The field type `phonetic` isn't one of the default types, but must be defined by us as well:

	<fieldtype name="phonetic" stored="false" indexed="true" class="solr.TextField">
	    <analyzer>
	        <tokenizer class="solr.WhitespaceTokenizerFactory"/>
	        <filter class="solr.WordDelimiterFilterFactory" generateWordParts="1" catenateWords="1" />
	        <filter class="solr.LowerCaseFilterFactory" />
	        <filter class="org.example.search.NorwegianNamesFilterFactory" />
	    </analyzer>
	</fieldtype>

When a name is copied from the `name` field and passed through the tokenizer and filters specified under `analyzer`.
The result of this is then stored in the index.

The tokenizer simply splits names into different tokens using whitespace.
The first filter splits tokens further if they contain word delimiters, e.g. "Bob-Kåre" results in "Bob" and "Kåre", and the lowercase filter makes the search case insensitive.

The last filter, however, is one we'll need to provide.

## Make the new field searchable

*TODO: describe `solrconfig.xml`, discuss the **select** controller and the dismax query handler, and how it's used to enable searching on the phonetic field.*


## The FilterFactory

*TODO: discuss the filterfactories specified in the analyzer filter chain, and describe the implementation.*

## The Filter

*TODO: discuss the actual implementation of the filter class.*

## The Result

*TODO: present an example query with results to show the search working, and refer to the working example on github.*