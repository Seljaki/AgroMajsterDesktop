package scraper.bizi

import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import models.Company

fun scrapeBiziPage(url: String): Company? {
    skrape(BrowserFetcher) {
        request {
            this.url = url
        }
        response {
            htmlDocument {
                println(html)
            }
        }
    }
    return null
}