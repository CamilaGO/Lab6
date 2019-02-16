package com.example.camila.lab6
// Paula Camila Gonzalez Ortega - Carnet 18398
// * Seccion 10
// * Esta clase permite crear objetos de tipo song que seran
// * utilizados en la lista del main para llevar control de las canciones disponibles

class Song(){
    private var id: Long = 0
    private var title: String = ""
    private var artist: String = ""

    constructor(songID: Long, songTitle: String, songArtist: String) : this() {
        this.id = songID
        this.title = songTitle
        this.artist = songArtist
    }

    fun getID(): Long {
        return id
    }

    fun getTitle(): String {
        return title
    }

    fun getArtist(): String {
        return artist
    }
}