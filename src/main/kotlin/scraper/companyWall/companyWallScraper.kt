package scraper.companyWall

import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.and
import it.skrape.selects.html5.a
import it.skrape.selects.html5.div
import it.skrape.selects.html5.span
import models.Company
import scraper.emailRegex
import scraper.getDocumentByUrl
import scraper.ibanRegex

/*fun scrapeCompanyWallPage(urlToScrape: String) {
    val html = getDocumentByUrl(urlToScrape)
    htmlDocument(html.html) {
        div {
            withAttribute = "itemtype" to "http://schema.org/Organization"
            findFirst {
                // name
                println(text)
            }
        }
        "small" {
            withAttribute = "itemtype" to "http://schema.org/PostalAddress"
            findFirst {
                // address
                println(text.replace(" ,", ","))
            }
        }
        span {
            withAttribute = "itemprop" to "vatID"
            findFirst {
                // davčna
                println(text.replace(" ", ""))
            }
        }
        div {
            withClass = "contact-summary"
            findFirst {
                println(text)
                val emailMatch = emailRegex.find(text)
                if (emailMatch != null) {
                    // email
                    println(emailMatch.value)
                }
            }
        }
        div {
            withClass = "bank-account-container"
            findFirst {
                val ibanMatch = ibanRegex.find(text)
                if (ibanMatch != null) {
                    // iban
                    println(ibanMatch.value)
                }
            }
        }
    }
}*/

fun scrapeCompanyWallPage(urlToScrape: String): Company {
    val company: Company = skrape(BrowserFetcher) { // <--- pass BrowserFetcher to include rendered JS
        request { url = urlToScrape }
        extractIt<Company> {c ->
            htmlDocument {
                div {
                    withAttribute = "itemtype" to "http://schema.org/Organization"
                    findFirst {
                        // name
                        c.name = text
                    }
                }
                "small" {
                    withAttribute = "itemtype" to "http://schema.org/PostalAddress"
                    findFirst {
                        // address
                        c.address = text.replace(" ,", ",")
                    }
                }
                span {
                    withAttribute = "itemprop" to "vatID"
                    findFirst {
                        // davčna
                        c.taxNumber = text.replace(" ", "")
                    }
                }
                div {
                    withClass = "contact-summary"
                    findFirst {
                        val emailMatch = emailRegex.find(text)
                        if (emailMatch != null) {
                            // email
                            c.email = emailMatch.value
                        }
                    }
                }
                div {
                    withClass = "bank-account-container"
                    findFirst {
                        val ibanMatch = ibanRegex.find(text)
                        if (ibanMatch != null) {
                            // iban
                            c.iban = ibanMatch.value
                        }
                    }
                }
            }
        }
    }

    return company
}