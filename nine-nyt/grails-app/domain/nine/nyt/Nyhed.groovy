package nine.nyt

class Nyhed {

    String titel
    String forfatter
    String tekst

    static mapping = {
        tekst type: 'text'
    }

    static constraints = {
        titel blank: false, nullable: false
        forfatter blank: false, nullable: false
        tekst blank: false, nullable: false
    }

    static searchable = {
        titel boost: 2.0
        tekst boost: 1.0
    }

}
