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
                <el-tab-pane label="转移记录" name="second"></el-tab-pane>
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
                <el-tab-pane
                    label="阶段性成果展示"
                    name="sixth"
                ></el-tab-pane>
                <el-tab-pane
                    label="打回"
                    name="seventh"
                ></el-tab-pane>
            </el-tabs>
        </div>
        <div class="m-t-32px">
				<Detail :detail="detailInfo" v-if="activeName == 'first'"> </Detail>
                <MoveTask :detail="detailInfo" v-else-if="activeName == 'second'"></MoveTask>
                <Working :detail="detailInfo" v-else-if="activeName == 'third'"></Working>
				<Pending :detail="detailInfo" v-else-if="activeName == 'fourth'"> </Pending>
				<FinishInfo :detail="detailInfo" v-else-if="activeName == 'fifth'"> </FinishInfo>
				<History :detail="detailInfo" v-else-if="activeName == 'sixth'"></History>
				<TrackBack :detail="detailInfo" v-else-if="activeName == 'seventh'"></TrackBack>
			</div>
    </div>
</template>
<script lang="tsx" setup>
import { onMounted, reactive, ref } from "vue";
import Detail from './deatil.vue'
import Working from './working.vue'
import MoveTask from './move-list.vue'
import Pending from './pending.vue'
import FinishInfo from './finishInfo.vue'
import History from './history.vue'
import TrackBack from './track-back.vue'
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