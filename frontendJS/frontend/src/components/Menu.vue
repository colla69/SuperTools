<template>
    <v-toolbar dense id="menu">
        <router-link to="/">
        <v-btn id="menuBtn" >
            Home
        </v-btn>
        </router-link>
        <router-link to="/visitors">
            <v-btn id="menuBtn" >
                vis
            </v-btn>
        </router-link>
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
