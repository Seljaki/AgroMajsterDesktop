package scraper.gerk

import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.eachLink
import it.skrape.selects.html5.a
import models.Company
import scraper.bizi.scrapeBiziPage

fun scrapeGERForLink(urlToScrape: String = "https://rkg.gov.si/arhiv/GERK/"): String? {
    var link: String? = null
    skrape(BrowserFetcher) { // <--- pass BrowserFetcher to include rendered JS
        request {
            url = urlToScrape
            timeout = 15000

        }
        response {
            htmlDocument {
                //println(text)
                a {
                    findAll {
                        //println(eachLink)
                        for (entry in eachLink.entries.iterator()) {
                            if(entry.value.contains(".zip")) {
                                link = "https://rkg.gov.si/arhiv/GERK/" + entry.value
                                return@findAll
                            }
                        }
                    }
                }
            }
        }
    }

    return link
}