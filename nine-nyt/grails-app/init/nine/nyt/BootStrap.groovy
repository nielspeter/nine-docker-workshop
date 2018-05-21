package nine.nyt

class BootStrap {

    def nyheder = [
            [
                    titel    : 'Ny kollega',
                    forfatter: 'Nine',
                    tekst    : 'D. 1. juni bliver Lea Tolstrup Husum en del af Nine-holdet. Mange af jer kender Lea fra den tid, hvor hun var ansat som Application Portfolio Manager/Product Owner hos Erhvervsstyrelsen.'
            ],
            [
                    titel    : 'Ny direktør',
                    forfatter: 'Nine',
                    tekst    : 'Pr. 20. april bliver Louise Sparf en del af direktørgruppen i Nine. Louise har beskæftiget sig med digitalisering og ledelse i Skat de seneste 8 år – i de sidste 4 år som Underdirektør for Digital Udvikling. Tidligere har Louise stiftet bekendtskab med konsulentbranchen, som konsulent i Deloitte Business Consulting.'
            ],
            [
                    titel    : 'Firmamøde',
                    forfatter: 'Nine',
                    tekst    : 'De 20. april er firmamøde for alle. Denne gang mødes vi i Cinemateket kl. 16:00. Her kommer Sérgio Oliveira fra firmaet “Khora Virtual Reality".'
            ]
    ]

    NyhedService nyhedService

    def init = { servletContext ->
        nyheder.each {
            nyhedService.save(new Nyhed(it))
        }
    }

    def destroy = {
    }


}
