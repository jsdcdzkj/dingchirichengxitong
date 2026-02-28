<template>
	<div>
        <div h="40px" style="position: relative;border-bottom: 1px solid rgba(0,0,0,0.15)">
            <el-tabs
                v-model="activeName"
                class="demo-tabs"
            >
                <el-tab-pane
                    label="详情"
                    name="first"
                ></el-tab-pane>
                <el-tab-pane
                    label="工时"
                    name="third"
                ></el-tab-pane>
                <el-tab-pane
                    label="挂起信息"
                    name="fourth"
                ></el-tab-pane>
                <el-tab-pane
                    label="完成信息"
                    name="fifth"
                ></el-tab-pane>
            </el-tabs>
        </div>
        <div class="m-t-32px">
				<Detail :detail="detailInfo" v-if="activeName == 'first'"> </Detail>
                <Working :detail="detailInfo" v-else-if="activeName == 'third'"></Working>
				<Pending :detail="detailInfo" v-else-if="activeName == 'fourth'"> </Pending>
				<FinishInfo :detail="detailInfo" v-else-if="activeName == 'fifth'"> </FinishInfo>
			</div>
    </div>
</template>
<script lang="tsx" setup>
import { onMounted, reactive, ref } from "vue";
import Detail from './deatil.vue'
import Working from './working.vue'
import Pending from './pending.vue'
import FinishInfo from './finishInfo.vue'
import {ElTabs, ElTabPane} from "element-plus"


const props = defineProps({
		detailInfo: {
			type:Object,
			default: {}
		}
	});
const activeName = ref('first')
</script>
<style lang="scss" scoped>
.demo-tabs {
	bottom: 0px;
}
</style>