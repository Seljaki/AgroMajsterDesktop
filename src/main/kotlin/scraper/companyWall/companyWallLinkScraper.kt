package scraper.companyWall


import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.eachLink
import it.skrape.selects.html5.a
import it.skrape.selects.html5.div
import it.skrape.selects.html5.span
import models.Company
import scraper.bizi.scrapeBiziPage
import scraper.emailRegex
import scraper.ibanRegex

fun scrapeCompanyWallForCompanies(maxCompaniesToScrape: Int = 3, urlToScrape: String = "https://www.bing.com/search?q=site%3Acompanywall.si+%22Storitve+za+rastlinsko+pridelavo%22&qs=n&form=QBRE&sp=-1&lq=0&pq=site%3Acompanywall.si+%22storitve+za+rastlinsko+pridelavo%22&sc=0-54&sk=&cvid=544B3C281FFC41288D017B638994B06F&ghsh=0&ghacc=0&ghpl="): MutableList<Company> {
    val companies = mutableListOf<Company>()
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
                            if(entry.value.contains("www.bizi.si/") && !entry.value.contains("TSMEDIA")) {
                                try {
                                    println("Scraping: ${entry.value}")
                                    val company = scrapeBiziPage(entry.value)
                                    if (companies.count() == 0) {
                                        companies.add(company)
                                        println("Scraped: ${company.name}")
                                    } else if(companies.count() > 0 && company.name != companies.last().name) {
                                        companies.add(company)
                                        println("Scraped: ${company.name}")
                                    }
                                } catch (ex: Exception) {
                                    println(ex)
                                }
                                //println("${entry.key} : ${entry.value}")
                            }
                            if(companies.count() >= maxCompaniesToScrape)
                                return@findAll
                        }
                    }
                }
            }
        }
    }

    return companies
}