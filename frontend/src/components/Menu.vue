<template>
    <v-toolbar dense  id="menu">
        <router-link to="/">
        <v-btn id="menuBtn" >
            Home
        </v-btn>
        </router-link>
        <router-link to="/visitors">
            <v-btn id="menuBtn" >
                CV Guests
            </v-btn>
        </router-link>
        <router-link to="/downloader">
            <v-btn id="menuBtn" >
                Downloader
            </v-btn>
        </router-link>
        <a href="https://dash.colarietitosti.info/jupyter">
            <v-btn id="menuBtn" >
                Jupyter Lab
            </v-btn>
        </a>
    </v-toolbar>
</template>

<script>
    export default {
        data () {
            return {
                items: [
                    { title: 'Home', icon: 'dashboard' },
                    { title: 'About', icon: 'question_answer' },
                ],
            }
        },
    }
</script>

<style scoped>
    #menu{
        padding: 0px;
        background: lightskyblue;
        margin-left: 10px;
        margin-right: 10px;
        margin-top: 3px;
    }
    #menuBtn{
        margin-right: 5px;
    }
</style>
