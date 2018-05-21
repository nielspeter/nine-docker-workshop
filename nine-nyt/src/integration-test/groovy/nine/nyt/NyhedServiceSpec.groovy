package nine.nyt

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class NyhedServiceSpec extends Specification {

    NyhedService nyhedService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Nyhed(...).save(flush: true, failOnError: true)
        //new Nyhed(...).save(flush: true, failOnError: true)
        //Nyhed nyhed = new Nyhed(...).save(flush: true, failOnError: true)
        //new Nyhed(...).save(flush: true, failOnError: true)
        //new Nyhed(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //nyhed.id
    }

    void "test get"() {
        setupData()

        expect:
        nyhedService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Nyhed> nyhedList = nyhedService.list(max: 2, offset: 2)

        then:
        nyhedList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        nyhedService.count() == 5
    }

    void "test delete"() {
        Long nyhedId = setupData()

        expect:
        nyhedService.count() == 5

        when:
        nyhedService.delete(nyhedId)
        sessionFactory.currentSession.flush()

        then:
        nyhedService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Nyhed nyhed = new Nyhed()
        nyhedService.save(nyhed)

        then:
        nyhed.id != null
    }
}
