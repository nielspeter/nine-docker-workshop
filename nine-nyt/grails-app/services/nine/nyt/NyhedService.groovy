package nine.nyt

import grails.gorm.services.Service

@Service(Nyhed)
interface NyhedService {

    Nyhed get(Serializable id)

    List<Nyhed> list(Map args)

    Long count()

    void delete(Serializable id)

    Nyhed save(Nyhed nyhed)

}