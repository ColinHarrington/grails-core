/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grails.web.mapping

import grails.web.mapping.UrlMapping
import org.springframework.mock.web.MockServletContext
import spock.lang.Specification
import spock.lang.Unroll

import java.util.regex.Matcher
import java.util.regex.Pattern

class RegexUrlMappingSpec extends Specification {

    @Unroll("url:#url => pattern:'#expectedPattern'  and url:#urlToMatch should match? #shouldMatch")
    void "Testing parsing of RegexUrlMappings"() {
        given:
            def parser = new DefaultUrlMappingParser()
            def servletContext = new MockServletContext()
            def mapping = new RegexUrlMapping(parser.parse('/something'), "test",
                    null, null, null, null, null, UrlMapping.ANY_VERSION, null, servletContext)

        when:
            Pattern pattern = mapping.convertToRegex(url)
            Matcher matcher = pattern.matcher(urlToMatch)

        then:
            pattern.pattern() == expectedPattern
            matcher.matches() == shouldMatch

        where:
            url                | urlToMatch           | shouldMatch | expectedPattern
            '/api/work(.(*))?' | '/api/work'          | true        | '^/api/work(\\.([^/]+))??/??$'
            '/api/work(.(*))?' | '/api/work.'         | false       | '^/api/work(\\.([^/]+))??/??$'
            '/api/work(.(*))?' | '/api/work.xml'      | true        | '^/api/work(\\.([^/]+))??/??$'
            '/api/work(.(*))?' | '/api/work.json'     | true        | '^/api/work(\\.([^/]+))??/??$'
            '/api/work(.(*))?' | '/api/worksheet'     | false       | '^/api/work(\\.([^/]+))??/??$'
            '/api/work(.(*))?' | '/api/workjson'      | false       | '^/api/work(\\.([^/]+))??/??$'

//            '/api/work(.(*))'  | '/api/work'          | false       | '^/api/work(\\.([^/]+))?/??$'
            '/api/work(.(*))'  | '/api/work.'         | false       | '^/api/work(\\.([^/]+))?/??$'
            '/api/work(.(*))'  | '/api/work.xml'      | true        | '^/api/work(\\.([^/]+))?/??$'
            '/api/work(.(*))'  | '/api/work.foo'      | true        | '^/api/work(\\.([^/]+))?/??$'

            '/some(.(*))?'     | '/'                  | false       | '^/some(\\.([^/]+))??/??$'
            '/some(.(*))?'     | '/some'              | true        | '^/some(\\.([^/]+))??/??$'
            '/some(.(*))?'     | '/some.'             | false       | '^/some(\\.([^/]+))??/??$'
            '/some(.(*))?'     | '/some.json'         | true        | '^/some(\\.([^/]+))??/??$'
            '/some(.(*))?'     | '/some.xml'          | true        | '^/some(\\.([^/]+))??/??$'
            '/some(.(*))?'     | '/some.cfg.txt'      | true        | '^/some(\\.([^/]+))??/??$'
            '/some(.(*))?'     | '/something.gson'    | false       | '^/some(\\.([^/]+))??/??$'

            'some(.(*))?'      | 'some'               | true        | '^some(\\.([^/]+))??/??$'
            'some(.(*))?'      | 'some.'              | false       | '^some(\\.([^/]+))??/??$'
            'some(.(*))?'      | 'some.json'          | true        | '^some(\\.([^/]+))??/??$'
            'some(.(*))?'      | 'some.thing.else'    | true        | '^some(\\.([^/]+))??/??$'

            '/bar/(*)(.(*))'   | '/bar/foo'           | true        | '^/bar/([^/]+)(\\.([^/.]+))?/??$'
//            '/bar/(*)(.(*))'   | '/bar/foo.'          | false       | '^/bar/([^/]+)(\\.([^/]+))?/??$'
            '/bar/(*)(.(*))'   | '/bar/foo.extension' | true        | '^/bar/([^/]+)(\\.([^/.]+))?/??$'


    }
}


