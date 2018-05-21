package nine.nyt

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        get "/nyhed/search/$q?"(controller: 'nyhed', action: "search")

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
