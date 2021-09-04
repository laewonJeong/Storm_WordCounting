# Apache Storm
- ### 데이터의 스트림을 처리하는 시스템
  - 실시간 데이터 처리 목적
- ### 주요 특징
  - 프로세싱 병렬 실행<br>
    -> 클러스터에서 연산 수행
  - 안전성
  - 내고장성(fault-tolerant)
---
# Topology/Cluster
- ### Topology
  - 데이터 처리 프로그램 단위
  - 데이터의 흐름을 정의
  - 데이터 변환을 구현
  - 물리적으로 jar 파일 형태로 클러스터에 전송
- ### Cluster
  - 토폴로지를 실행
  - 다수의 JVM에서 토폴로지의 작업을 병렬 실행
---
# Storm Topology
![image](https://user-images.githubusercontent.com/81546637/132097540-8336d938-4d63-4467-a59b-24887da1c30f.png)
- ### DataStream, Spout, Bolt로 구성
- ### Hadoop과 같은 배치 처리 시스템의 Job과 거의 비슷
  - But, StormTopology는 kill 하거나 undeploy할 때까지 계속 동작 
- ### DataStream
  - 스톰의 기본 데이터 구조체: Tuple
  - Tuple = 네임드 벨류의 목록
    - ex) Tuple = [key: value, key: value, ...] 형식의 데이터
  - Stream = 연속된 Tuple
- ### Spout
  - 데이터가 스톰 토폴로지로 들어가는 입구
  - 외부 메시징 시스템 등에서 데이터 가져옴
  - 데이터를 Tuple로 변환하여 Stream으로 Bolt에 전달
- ### Bolt
  - 실시간 연산의 연산자 or 함수로 볼 수 있음
  - Spout나 다른 Bolt로 부터 Stream을 받음
  - 데이터를 처리해서 다른 Bolt에 전달하거나 외부에 저장
---
# Storm Parallelism
- ### 스톰은 연산을 다수의 장비를 이용해 선형적으로 확장할 수 있음
  - 다수의 작고 독립적인 task로 나누고 cluster 전체에 나누어 병렬을 처리할 수 있기 때문
- ### Major Component in Storm Cluster to execute Topology
  - ### Node
    - Storm Cluster에 포함된 장비(서버)
    - 실제로 Topology가 동작하는 곳
    - 한 개 이상의 Node를 포함함
  - ### Worker(JVM)
    - Node에서 동작하는 독립적인 JVM 프로세스
    - Topology는 하나 이상의 worker에서 동작하도록 배치됨
  - ### Executor(Thread)
    - Worker JVM 프로세스에서 동작하는 자바 스레드
    - 단일 Executor에 다수의 작업단위를 배치할 수 있음
    - 기본적으로 각 Executor는 한 개의 작업 단위만 할당 받음
  - ### Task(Bolt/Spoutinstance)
---
# Storm Cluster
![image](https://user-images.githubusercontent.com/81546637/132098059-cacef3ca-4757-4309-b448-53ec9f1b2ffb.png)
- ### Storm Cluster는 Hadoop Cluster와 같이 Master/Slaves 구조를 따르지만 조금 다름
- ### There are two kinds of nodes on a Storm Cluster
  - Master Node
  - Worker Node
- ### Master Node
  - Nimbus라고 하는 한 개의 Master Node로 구성
- ### Worker Node
  - Supervisor라고 하는 다수의 Worker Node로 구성
- ### Nimbus와 Supervisor 프로세스는 Storm이 제공하는 demon process
- ### Nimbus
  - 클러스터에서 실행되는 토폴로지를 관리, 조절, 모니터링 하는 것
  - 토폴로지를 클러스터로 배포
  - 토폴로지에 task를 할당하고 작업단위에 장애가 생기면 작업단위를 다시 할당하는 일을 함
- ### Supervisor
  - Nimbus로 부터 task를 할당 받음
  - Task를 실행하기 위해 worker 프로세스를 생성 및 worker 모니터링
- ### Zookeeper
  - 몇가지 기본 기능과 그룹 기능을 제공하는 분산환경용 중앙 정보 저장소 서비스
  - 클러스터 내의 님버스와 수퍼바이저 간에 작업단위 할당 정보, 워커 상태 정보, 토폴로지 메트릭 같은 상태 정보를 공유하기 위해 주키퍼를 사용        
---
