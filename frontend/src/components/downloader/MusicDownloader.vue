<template>
    <div>
        <v-card id="musiclayout" raised>
            <h3>Download Artist</h3>
            <v-text-field dense clearable
                          label="Artist"
                          placeholder="Artist Name"
                          v-model="artist"
            >
            </v-text-field>
            <v-text-field clearable dense
                          label="linkpart"
                          placeholder="linkpart from discogs.com"
                          v-model="linkpartArtist"
            >
            </v-text-field>
            <v-layout reverse row style="padding-right: 10px;">
                <v-btn @click="startArtistDwld" outlined small>Start Download</v-btn>
                <v-btn @click="resetArtist" outlined small style="margin-right: 10px;">Reset</v-btn>
            </v-layout>
        </v-card>
        <v-card id="musiclayout" raised>
            <h3>Download Album</h3>
            <v-text-field clearable dense
                          label="Artist"
                          placeholder="Artist Name"
                          v-model="artist"
            >
            </v-text-field>
            <v-text-field clearable dense
                          label="Album"
                          placeholder="Album Name"
                          v-model="album"
            >
            </v-text-field>
            <v-text-field dense clearable
                          label="linkpart"
                          placeholder="linkpart from discogs.com"
                          v-model="linkpartAlbum"
            >
            </v-text-field>
            <v-layout row reverse style="padding-right: 10px;">
                <v-btn @click="startAlbumDwld" outlined small>Start Download</v-btn>
                <v-btn @click="resetAlbum" outlined small style="margin-right: 10px;">Reset</v-btn>
            </v-layout>
        </v-card>
    </div>
</template>

<script>
    import axios from 'axios';

    export default {
        name: "MusicDownloader",
        data() {
            return {
                artist: "",
                album: "",
                linkpartArtist: "",
                linkpartAlbum: ""
            }
        },
        methods: {
            resetArtist: function () {
                this.artist = "";
                this.linkpartArtist = "";
            },
            resetAlbum: function () {
                this.artist = "";
                this.album = "";
                this.linkpartAlbum = "";
            },
            startArtistDwld: function () {
                axios.post('/backend/startArtistDownload',
                    {
                        artist: this.artist,
                        linkpart: this.linkpartArtist
                    });
            },
            startAlbumDwld: function () {
                axios.post('/backend/startAlbumDownload',
                    {
                        artist: this.artist,
                        album: this.album,
                        linkpart: this.linkpartAlbum
                    });
            }
        }
    }
</script>

<style scoped>
    h3 {
        margin-bottom: 10px;
    }
    #musiclayout {
        margin-bottom: 5px;
        padding: 10px;
        display: block;
    }
</style>