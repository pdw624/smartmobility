import { Notification } from 'element-ui'


const Util = {
    install(Vue, options) {
        Vue.prototype.notification = (toastParam) => {
            const toast = {
                // 동작 팝업 메시지
                saveAction: [ 'success', '저장 성공', '동작내용이 저장되었습니다'],
                deleteAction: [ 'success', '삭제', '동작이 삭제되었습니다' ],
                deleteActionCheck: [ 'info', '삭제', '삭제할 동작을 체크해주세요' ],
                deleteActionError: [ 'error', '삭제', '동작삭제중 오류가 발생하였습니다' ],
                executeAction: [ 'success', '실행 완료', '통계조회에서 결과 확인이 가능합니다' ],
                executeActionCheck: [ 'info', '실행', '실행할 동작을 체크해주세요' ],
                actionIsRun: [ 'info', '실행중', '동작이 실행중입니다. 잠시 후 시도해주세요.' ],
                
                // 시나리오 팝업 메시지
                saveScenario: [ 'success', '저장 성공', '시나리오 내용이 저장되었습니다' ],
                saveScenarioCheck: [ 'info', '시나리오 저장', '동작을 1개이상 등록해주세요' ],
                deleteScenario: [ 'success', '삭제', '시나리오가 삭제되었습니다' ],
                deleteScenarioCheck: [ 'info', '삭제', '삭제할 시나리오를 체크해주세요' ],
                deleteScenarioError: [ 'error', '삭제', '시나리오삭제중 오류가 발생하였습니다' ],
                executeScenario: [ 'success', '실행 완료', '통계조회에서 결과 확인이 가능합니다' ],
                executeScenarioCheck: [ 'info', '실행', '실행할 시나리오를 체크해주세요' ],

                // 예약 팝업 메시지
                saveReserveScenarioCheck: [ 'info', '예약 시나리오 저장', '동작을 1개이상 등록해주세요' ],
                overlapReserveScenario: [ 'info', '예약 중복', '예약 날짜가 겹칩니다. 예약관리에서 확인해주세요.' ],
                isExecuteReserveScenario: [ 'info', '예약 실행중', '곧 예약이 시작됩니다. 잠시 후 시도해주세요.' ],
                reserveTimeCheck: [ 'info', '예약', '예약 날짜를 알맞은 형식으로 입력해주세요' ],
                deleteReserve: [ 'success', '삭제', '예약이 삭제되었습니다' ],
                deleteReserveError: [ 'error', '삭제', '예약삭제중 오류가 발생하였습니다' ],
                deleteReserveCheck: [ 'info', '삭제', '삭제할 예약을 체크해주세요' ],
                pause: [ 'info', '실행', '실행중인 작업이 중지되었습니다' ],

                // 통계조회 팝업 메시지
                downloadExcel: [ 'info', '다운로드', '데이터가 로딩중입니다']
            }

            return Notification({
                type: `${toast[toastParam][0]}`,
                title: `${toast[toastParam][1]}`,
                message: `${toast[toastParam][2]}`,
                duration: 1500,
                offset: 80
            })
        }
    }
}

export default Util