package scraper.companyWall


import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.eachLink
import it.skrape.selects.html5.a
import it.skrape.selects.html5.div

fun scrapeBiziPageForLinks(urlToScrape: String = "https://www.bizi.si/iskanje/") {
    skrape(BrowserFetcher) { // <--- pass BrowserFetcher to include rendered JS
        request {
            url = urlToScrape
            timeout = 15000

        }
        response {
            htmlDocument {
                println(text)
                div {
                    withClass = "searched-companies"
                    findFirst {
                        a {
                            findAll {
                                println(eachLink)
                            }
                        }
                    }
                }
            }
        }
    }
}