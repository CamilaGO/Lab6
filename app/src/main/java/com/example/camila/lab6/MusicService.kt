package com.example.camila.lab6

import android.app.Service
import android.content.ContentUris
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.util.Log
import java.util.*

@Suppress("DEPRECATION")
class MusicService: Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener{

    //media player
    private var player: MediaPlayer = MediaPlayer()
    //song list and song data
    lateinit var songs: ArrayList<Song>
    private var songTitle = ""
    private var songArtist = ""
    //current position
    private var songPosn: Int = 0
    private val NOTIFY_ID = 1
    private val musicBind = MusicBinder()
    private var shuffle = false
    private var rand: Random? = null

    override fun onCreate() {
        //se crea el servicio
        super.onCreate()
        initMusicPlayer()
        rand = Random()
    }

    override fun onBind(arg0: Intent): IBinder? {
        return musicBind
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if(player.currentPosition >0)
            mp!!.reset()
            playNext()

    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        mp!!.reset()
        return false
    }

    override fun onPrepared(mp: MediaPlayer?) {
        //start playback
        mp?.start()

    }

    override fun onUnbind(intent: Intent): Boolean {
        player.stop()
        player.release()
        return false
    }

    fun initMusicPlayer(){
        //set player properties
        player.setWakeMode(applicationContext,
            PowerManager.PARTIAL_WAKE_LOCK)
        player.setAudioStreamType(AudioManager.STREAM_MUSIC)
        player.setOnPreparedListener(this)
        player.setOnCompletionListener(this)
        player.setOnErrorListener(this)
    }

    //Se pasan la lista de canciones del main al servicio
    fun setList(theSongs: ArrayList<Song>) {
        songs = theSongs
    }

    inner class MusicBinder : Binder() {
        internal val service :MusicService
            get() = this@MusicService
    }

    fun playSong(){
        //se reproduce la cancion
        player.reset()
        //get song
        val playSong = songs[songPosn]
        //get artist
        songArtist=playSong.getArtist()
        //get title
        songTitle=playSong.getTitle()
        //get id
        val currSong = playSong.getID()
        //set uri
        val trackUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            currSong
        )
        try {
            player.setDataSource(applicationContext, trackUri)
        } catch (e: Exception) {
            Log.e("MUSIC SERVICE", "Error setting data source", e)
        }
        player.prepareAsync()

    }

    fun setSong(songIndex: Int){
        songPosn = songIndex
    }

    fun getPosn(): Int {
        return player.currentPosition
    }

    fun getDur(): Int {
        return player.duration
    }

    fun isPng(): Boolean {
        return player.isPlaying
    }

    fun pausePlayer() {
        player.pause()
    }

    fun seek(posn: Int) {
        player.seekTo(posn)
    }

    fun go() {
        player.start()
    }

    fun playPrev() {
        songPosn--
        if (songPosn<0)
            songPosn = songs.size -1
        playSong()
    }

    //skip to next
    fun playNext(){
        songPosn++
        if (songPosn == songs.size)
            songPosn = 0
        playSong()
    }

    override fun onDestroy() {
        stopForeground(true)
    }

}