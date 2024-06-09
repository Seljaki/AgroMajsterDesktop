package scraper

import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import scraper.companyWall.scrapeBiziPageForCompanies
import scraper.companyWall.scrapeCompanyWallForCompanies

val ibanRegex = Regex("[A-Z]{2}\\d{2} ?\\d{4} ?\\d{4} ?\\d{4} ?\\d{3}")
val emailRegex = Regex("[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}")

fun getDocumentByUrl(urlToScrape: String) = skrape(BrowserFetcher) { // <--- pass BrowserFetcher to include rendered JS
    request { url = urlToScrape }
    response { htmlDocument { this } }
}

fun main() {
    val companies = scrapeBiziPageForCompanies()
    //val companies = scrapeCompanyWallForCompanies()
    println("=== LISTING ALL FOUND COMPANIES ===")
    for(company in companies) {
        println(company)
    }
    //scrapeBiziPageForLinks()
    //scrapeCompanyWallForCompanies()
    /*val petrol2 = scrapeCompanyWallPage("https://www.companywall.si/podjetje/petrol-dd-ljubljana/MM58g0oY?recaptchaToken=03AFcWeA4iiRU_NU9rrzsYChBcTsRCHtKkyK3SB8dDpa2njNy84NFn0TZ15dSF6aaCukmt_HGg-LiG3f-xp2EcRzLvx1d02QL0eEl7Mb0kzZyHs81xoosTfsjA5JQybX8ipSa7oX-J5gbKEwsbLgXDdeEb25bV6sJITRkNtRoNxFEoC5hAem2eBgV85JsDvHcCM0n4-IwZtH6YOIup4zRxkddrxjZSk9T1nX08bTmkko_WPIzszP0PsPqzfxLcmS85Gv3FHGYbRV4rAGNssbX-AM56mQw3hx_jesYTKcdAyJEqRAWxwoxNOg9c4UusCK5Ctm9njV63-FSLXvHjgrP3jDo4fzhD5uE7RmGHJWTqAWqRjEIWbe3ajPgL7g6_VBHO9izgbL7kJn9B279gB81AoLBT0_qVoq4V6AaGei5eDYD6huI7y9sniPL7EAsQ66uXZ7-EzIO2I3DdaHnd-KtL9DXB8-hrmQKCpNtGRCh2aXW3xUxOdp2Ri94P_6Z5pLzqzEZ9VMVupEOZy69OXOWCzMQDeNJUchu6EkwgCaAUPMyY_LYHLm1lkJm4cYLrqwKJE3b9fDd_ozgFJka1b25OKoIckFkXQrJnlwuMfs-Git0RsMXCdpCfVMJ4zp7T7le0BC8DRFRrn9BPMzYBTSo1DsrJ9MqHvHdqtRyUXfbkr8jPxzlgOLbtWlk")
    val steng2 = scrapeCompanyWallPage("https://www.companywall.si/podjetje/steng-nacionalni-center-za-cistejso-proizvodnjo-doo/MMERebQD?recaptchaToken=03AFcWeA7VyM3eWiyqbmW8JqBj15Hza5ylWFRbPaGEe_nSNtk5s9xOpDfawxfnYWdEnLsJzL_UYSSFTpZN3CyeL5TJtXhL5JMgaYSlNIO6zsrxVGZQ9He84sneNom9VPvTlCHkmVf_Xx4H8AOUApw_gwYmzBuoRboTo9TTP3k12DQpN1MBrdQ--REQ45oVWgcV2_tAMwSdh0fceyaC5RyxutC-nQhJ68cSMi_NOBV5LGqJKevViG7-mpWnRwubw3pW3SIJfhX1d8nqYvptH-jwb7iag8JCxuzwr5F_M-cXfMIRHO66zmxzz4qjVX-Dy01T6eqbytiZtyiAJ7XMouFuTZ6gACtkLZg6xJ0G3ndwsu1LkHgo3jnEp9Y1SlpPj2aBuZ37q1yINiZFCO7SadHU9nHx-rKg0AWyhvz5Cd1-fe43UWi6VRSNAj3Gp6WA_3g9zXtiHp-LAcAc_RRXmF-7bTen0kgn0bJF_frxLoJj-tFyoMywbdAhQ068spUEHL9OQCe7URCWVu79j8EwW7BXnqscs4L9LrZSRS24BpFy821An24rb-2ZVG1vbs851pfqZzN1ZZCprkbW_WR4hikSM_lwAoVB1C4K_wLEV40H2ZZcDTtRB6E2BPvmAZC-yr5us01udakhPdgoFFqvFEhqyzEHwivLzceiUhu_-k3eqRA3K-z5ISSP_Ec")
    println(petrol2)
    println(steng2)*/
    /*val petrol = scrapeBiziPage("https://www.bizi.si/PETROL-D-D-LJUBLJANA/")
    val agroCvetko = scrapeBiziPage("https://www.bizi.si/AGRO-CVETKO-D-O-O/")
    val steng = scrapeBiziPage("https://www.bizi.si/STENG-NACIONALNI-CENTER-ZA-CISTEJSO-PROIZVODNJO-D-O-O/")

    println(petrol)
    println(agroCvetko)
    println(steng)*/
}