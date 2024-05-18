package scraper

import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import scraper.bizi.scrapeBiziPage

val ibanRegex = Regex("[A-Z]{2}\\d{2} ?\\d{4} ?\\d{4} ?\\d{4} ?\\d{3}")

fun getDocumentByUrl(urlToScrape: String) = skrape(BrowserFetcher) { // <--- pass BrowserFetcher to include rendered JS
    request { url = urlToScrape }
    response { htmlDocument { this } }
}
fun main() {
    val petrol = scrapeBiziPage("https://www.bizi.si/PETROL-D-D-LJUBLJANA/")
    val agroCvetko = scrapeBiziPage("https://www.bizi.si/AGRO-CVETKO-D-O-O/")

    println(petrol)
    println(agroCvetko)
}