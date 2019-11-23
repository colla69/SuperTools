<template>
    <v-toolbar dense id="menu">
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
        width: 100%;
        display: block;
        background: lightskyblue;
        margin: 10px;
    }
    #menuBtn{
        margin-right: 5px;
    }
</style>
