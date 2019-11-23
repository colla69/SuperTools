<template>
    <v-expansion-panels id="musicpanel">
        <v-expansion-panel>
            <v-expansion-panel-header>Music Downloader</v-expansion-panel-header>
            <v-expansion-panel-content>
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
                <v-layout reverse>
                    <v-btn small >Start Download</v-btn>
                    <v-btn small style="margin-right: 10px;" @click="reset">Reset</v-btn>
                </v-layout>
            </v-expansion-panel-content>
        </v-expansion-panel>
    </v-expansion-panels>
</template>

<script>
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
            }
        }
    }
</script>

<style scoped>
    #musicpanel {
        padding: 10px;
    }
</style>