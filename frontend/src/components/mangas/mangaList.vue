<template>
    <v-card id="container" >
        {{title}}
        <br><br><br>
        {{html}}
    </v-card>
</template>

<script>
    //import {doStuff} from './scraper';
    import axios from "axios";
    import cheerio from "cheerio";

    export default {
        name: "mangaList",
        data: function(){
            return {
            html: "",
            title: ""
        }},
        mounted() {
            getChapters(this);
        }
    }
    function getChapters(view) {
        axios.get("/mangas/one-piece")
            .then(response => {
                const html = response.data;
                const selector = cheerio.load(html);
                const txt = selector("table[class='d48']").text();

                view.html = html;
                view.title = txt;
            });
    }
</script>

<style scoped>
    #container{
        margin: 10px;
    }
</style>