package nine.nyt

import grails.validation.ValidationException

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK

class NyhedController {

    NyhedService nyhedService
    NyhedSearchService nyhedSearchService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def search(String q) {
        if (!q) {
            render status: NOT_FOUND
            return
        }
        Map result = nyhedSearchService.search(q)
        respond result.searchResults, model: [nyhedCount: result.total], view: 'index'
    }

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond nyhedService.list(params), model: [nyhedCount: nyhedService.count()]
    }

    def show(Long id) {
        respond nyhedService.get(id)
    }

    def create() {
        respond new Nyhed(params)
    }

    def save(Nyhed nyhed) {
        if (nyhed == null) {
            notFound()
            return
        }

        try {
            nyhedService.save(nyhed)
        } catch (ValidationException e) {
            respond nyhed.errors, view: 'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'nyhed.label', default: 'Nyhed'), nyhed.id])
                redirect nyhed
            }
            '*' { respond nyhed, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond nyhedService.get(id)
    }

    def update(Nyhed nyhed) {
        if (nyhed == null) {
            notFound()
            return
        }

        try {
            nyhedService.save(nyhed)
        } catch (ValidationException e) {
            respond nyhed.errors, view: 'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'nyhed.label', default: 'Nyhed'), nyhed.id])
                redirect nyhed
            }
            '*' { respond nyhed, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        nyhedService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'nyhed.label', default: 'Nyhed'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'nyhed.label', default: 'Nyhed'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
