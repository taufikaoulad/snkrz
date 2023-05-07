package cat.copernic.taufik.snkrz.Provider

import cat.copernic.taufik.snkrz.Model.sneaker

class Provider {
    companion object {
        // Objeto que contiene la lista de zapatillas
        val SneakerList = listOf<sneaker>(
            sneaker(
                "NDLGF123",
                "Nike Dunk Lows",
                "GREY FROG",
                "$99.99",
                "2022-06-01",
                "Una zapatilla de baloncesto icónica que se ha convertido en una pieza de moda"
            ),
            sneaker(
                "ASC123",
                "Adidas Superstar",
                "Classic",
                "$89.99",
                "2022-07-15",
                "El clásico que nunca pasa de moda"
            ),
            sneaker(
                "CCAS456",
                "Converse Chuck Taylor",
                "All Star",
                "$79.99",
                "2022-08-10",
                "El favorito de todos los tiempos de la cultura pop"
            )
        )
    }
}