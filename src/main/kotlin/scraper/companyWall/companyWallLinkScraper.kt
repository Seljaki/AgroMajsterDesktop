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
import scraper.emailRegex
import scraper.ibanRegex

fun scrapeCompanyWallForCompanies(urlToScrape: String = "https://www.companywall.si/iskanje?cr=EUR&n=&mv=&r=&c=&cp=&FromDateAlt=&FromDate=&ToDateAlt=&ToDate=&at=&area=1&subarea=-1&sbjact=t&blckd=&dbf=&dbt=&type=&bly=2022&dsm%5B0%5D.Code=101&dsm%5B0%5D.From=0&dsm%5B0%5D.To=0&wfr=&wto=&dsm%5B-1%5D.Code=0&dsm%5B-1%5D.From=0&dsm%5B-1%5D.To=0&distinctcodes=&xpnd=true&recaptchaToken=03AFcWeA422GCNrw-yqffX_BHylkxLT5XIwijKWmTauocR-zxLt0ZIsLknZxZuDl9mbWF0HcQUQcoz8-5PsG2k3SCGF21Fh_xCqYWAoZHAUjE6Bwkk0kPnvctYhuoz3GQcaPaPC5Cvegeowlx5Nl4kq21kvRuuEWbZBGCUAf2dY3BcDg0u97efU_91jS-UUrxtfzroA0CigoVOujmMBovJPcpg0wGBkOYqfqlp2NC0rOAQ7hlLhgcnpqakHO3t_Uek1tNNI64gmJyRhnTkylOnEYzKpNTtYaHR7Osl5Ry6pOrvlJ0qWemHJR_Vxfq8z-n9FgY2D69e785NyNpd3Ora_NX8yZoVXUEaNMXHf4AncDUBhR6a6K1aHb04Estyw4ryqydXbIBo1deLmebvKSKt_wNSvoZk27dNr1ECJ_ABftjl7nj_z4_VM5p2g_1okXSRjBrVK12dJrmNF_OCy_Cm8VgF0BY5egdJlxzAODf6H7eCd0rHcvFmUiqD-wdiL8qcuZguHpSWf5xv-fHFWASqf4Xxc08qlQcvePHtKu7P0MX3PM9rGc5bHRJ7DtRRW2PnVk5YtepHGEYtLAC7VgOsSTRXOBJK_aQs9NaQeCM6DJogH97Y633RcSATXjqCKXQUQNVLK3rd0QN3KFbqcb47dYyyox1Kcz42kwI1lhcdv68uMlSMEVuO47c") {
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