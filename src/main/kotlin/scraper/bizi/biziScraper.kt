package scraper.bizi

import it.skrape.fetcher.*
import it.skrape.selects.html5.*
import it.skrape.core.htmlDocument
import it.skrape.fetcher.skrape
import it.skrape.selects.and
import it.skrape.selects.html5.div
import models.Company
import scraper.ibanRegex

fun scrapeBiziPage(urlToScrape: String): Company {
    val company: Company = skrape(BrowserFetcher) { // <--- pass BrowserFetcher to include rendered JS
        request {
            url = urlToScrape
            timeout = 15000
        }
        extractIt<Company> {c ->
            htmlDocument {
                div {
                    withClass = "b-title"
                    findFirst {
                        // name
                        c.name = text
                    }
                }
                div {
                    withClass = "b-contacts"
                    findFirst {
                        a {
                            withClass = "i-ostalo-lokacija"
                            findFirst {
                                c.address = text
                            }
                        }
                        a {
                            withClass = "i-ostalo-telefon"
                            findFirst {
                                c.phone = text
                            }
                        }
                        try {
                            a {
                                withClass = "i-orodja-ovojnice"
                                findFirst {
                                    // email
                                    c.email = text
                                }
                            }
                        } catch (e: Exception) {
                            println("Email not found")
                        }
                    }
                }
                div {
                    withClass = "b-box" and "col-12" and "col-md-6" and "col-xl-4"
                    findFirst {
                        val isTaxpayer = text.contains("Zavezanec za DDV: Da")
                        c.isTaxpayer = isTaxpayer
                        if(isTaxpayer) {
                            val match = "(SI\\d+)".toRegex().find(text)
                            if (match != null) {
                                // davčna
                                c.taxNumber = match.value
                            }
                        }
                    }
                }
                val ibanMatch = ibanRegex.find(text)
                if (ibanMatch != null) {
                    // iban
                    c.iban = ibanMatch.value
                }
            }
        }
    }

    return company
}