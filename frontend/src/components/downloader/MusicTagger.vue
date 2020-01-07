<template>
    <v-layout  id="musiclayout">
        <h3>Tag</h3>
        <v-list dense >
            <li v-for="(q,ix) in state.artists" v-bind:key="ix">
                <input type="checkbox"
                       :id="q"
                       :value="q.name"
                       :v-model="input"
                       @click="addTodoArtist(q)">
                <label style="padding-left: 5px;" :for="q">{{ q }}</label>
            </li>
        </v-list>
        <v-layout reverse>
            <v-btn small @click="startTag"  >Tag Artist</v-btn>
        </v-layout>
    </v-layout>
</template>

<script>
    import { reactive } from "@vue/composition-api";
    import axios from "axios";

    export default {
        name: "MusicTagger",
        setup() {
            const { state, addTodoArtist, startTag } = useTagger();
            return { state, addTodoArtist, startTag };
        }
    }

    function useTagger() {
        let state = reactive({
            working: false,
            artists: [],
            todo_artists: []
        });

        axios.get('/backend/downloadedArtists')
            .then( response => {
                state.artists = response.data
            } );

        function addTodoArtist(input){
            if (state.todo_artists.includes(input)){
                state.todo_artists.splice(input,1);
            } else {
                state.todo_artists.push(input);
            }
        }

        function startTag(){
            axios.post('/backend/startMusicTag', state.todo_artists);

        }
        return { state, addTodoArtist, startTag }
    }
</script>

<style scoped>
    li{
        min-height: 25px;
        text-align: left;

    }
    h3{
        margin-bottom: 10px;
    }
    .v-list{
        padding : 10px;

    }
    #musiclayout {
        padding: 10px;
        display: block;
    }
</style>