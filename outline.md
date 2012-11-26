Bloggpost outline
=================

- Hva er Solr/Lucene?
- Sette opp query handler DisMax i solrconfig.xml

		<requestHandler name="/select" class="solr.SearchHandler">
		<lst name="defaults">
		   <str name="echoParams">explicit</str>
		   <int name="rows">10</int>
		   <str name="df">navn</str>
		 <str name="defType">dismax</str>
		   <str name="qf">
		      nummer^10 navn^3 navn_fonetisk^1
		   </str>
		   <str name="q.alt">*:*</str>
		   <str name="fl">*,score</str>
		</lst>
		</requestHandler>

- Hvordan navn og navn_fonetisk er satt opp i schema.xml

		<types>
			<fieldType name="text_general" class="solr.TextField" positionIncrementGap="100">
				<analyzer type="index">
					<tokenizer class="solr.StandardTokenizerFactory" />
					<filter class="solr.LowerCaseFilterFactory" />
				</analyzer>
				<analyzer type="query">
					<tokenizer class="solr.StandardTokenizerFactory" />
					<filter class="solr.LowerCaseFilterFactory" />
				</analyzer>
			</fieldType>
			<fieldtype name="phonetic" stored="false" indexed="true" class="solr.TextField">
			    <analyzer>
			        <tokenizer class="solr.WhitespaceTokenizerFactory"/>
			        <filter class="solr.LowerCaseFilterFactory" />
			        <filter class="no.example.search.NorwegianNameFilterFactory" />
			    </analyzer>
			</fieldtype>
		</types>

		<fields>
			<field name="navn" type="text_general" indexed="true" stored="false" multiValued="true" />
			<field name="navn_fonetisk" type="phonetic" indexed="true" stored="false" multiValued="true" />
		</fields>

		<copyField source="fornavn" dest="navn" />
		<copyField source="mellomnavn" dest="navn" />
		<copyField source="etternavn" dest="navn" />
		<copyField source="orgnavn" dest="navn" />

		<copyField source="navn" dest="navn_fonetisk" />
		<copyField source="mellomnavn" dest="navn_fonetisk" />
		<copyField source="etternavn" dest="navn_fonetisk" />
		<copyField source="orgnavn" dest="navn_fonetisk" />

- Forklare analyzer/filter chain.
- Vise og forklare implementasjon av NorwegianNameFilterFactory.java
- Vise og forklare NorwegianNameFilter.java
- Underveis referere til Norphone.java
  - Linke til vår Norphone.java på GitHub, og/eller der vi fant den.
- ???
- Profit!