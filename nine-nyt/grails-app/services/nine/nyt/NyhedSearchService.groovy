package nine.nyt

import grails.plugins.elasticsearch.ElasticSearchService

class NyhedSearchService {

    ElasticSearchService elasticSearchService

    Map search(String query) {
        elasticSearchService.search(query, [indices: Nyhed, types: Nyhed, score: true])
    }

}
