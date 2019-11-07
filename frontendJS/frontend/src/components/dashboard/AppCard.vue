

<template>
    <div class="app">
        <img :src="imglink" class="img" alt="none">
        {{name}}
    </div>
</template>

<script>
    export default {
        name: "AppCard",
        props: {
            link: String,
            imglink: String,
            name: String
        }
    }
</script>

<style scoped>
    .img{
        max-width: 123px;
        max-height:123px;
        min-width: 123px;
        min-height:123px;
        box-sizing: border-box;
        border-radius: 15px;
        -moz-border-radius: 15px;
        -webkit-border-radius: 15px;
        border: 1px none  #000000;

    }
    .app {
        padding: 0px;
        margin: 7px;
        background-color: lightblue;
        opacity: .8;
        display: inline-block;

        height: 150px;
        min-height: 150px;
        max-height: 150px;
        max-width: 123px;
        min-width: 123px;

        box-sizing: border-box;
        border-radius: 15px;
        -moz-border-radius: 15px;
        -webkit-border-radius: 15px;
        border: 1px none  #000000;
        -webkit-box-shadow: 0px 1px 15px 4px rgba(0, 0, 0, 0.45);
        -moz-box-shadow: 0px 1px 15px 4px rgba(0, 0, 0, 0.45);
        box-shadow: 0px 1px 15px 4px rgba(0, 0, 0, 0.45);
    }

</style>