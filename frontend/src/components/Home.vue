<template>
    <div id="app" class="body">
        <AppCardsGroup title="Dashboard">
        </AppCardsGroup>
    </div>
</template>

<script>
    import AppCardsGroup from "./dashboard/AppCardsGroup";

    export default {
        name: 'home',
        components: {
            AppCardsGroup,
        }
    }
</script>
<style>

</style>