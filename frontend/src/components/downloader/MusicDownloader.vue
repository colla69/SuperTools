<template>
    <v-card raised id="musiclayout">
        <h3>Download</h3>
            <v-text-field dense clearable
                    v-model="artist"
                    label="Artist"
                    placeholder="Artist Name"
            >
            </v-text-field>
            <v-text-field dense clearable
                    v-model="linkpart"
                    label="linkpart"
                    placeholder="linkpart from discogs.com"
            >
            </v-text-field>
            <v-layout row reverse style="padding-right: 10px;">
                <v-btn outlined small  @click="startDwld" >Start Download</v-btn>
                <v-btn outlined small style="margin-right: 10px;" @click="reset">Reset</v-btn>
            </v-layout>
    </v-card>
</template>

<script>
    import axios from 'axios';
    export default {
        name: "MusicDownloader",
        data() {
            return {
                artist : "",
                linkpart : "",
            }
        },
        methods:{
            reset: function () {
                this.artist = "";
                this.linkpart = "";
            },
            startDwld: function () {
                axios.post('/backend/startMusicDownloads',
                     {
                        artist: this.artist,
                        linkpart: this.linkpart
                    });

            }
        }
    }
</script>

<style scoped>
    h3{
        margin-bottom: 10px;
    }
    #musiclayout {
        padding: 10px;
        display: block;
    }
</style>