package raft

//
// this is an outline of the API that raft must expose to
// the service (or tester). see comments below for
// each of these functions for more details.
//
// rf = Make(...)
//   create a new Raft server.
// rf.Start(command interface{}) (index, term, isleader)
//   start agreement on a new log entry
// rf.GetState() (term, isLeader)
//   ask a Raft for its current term, and whether it thinks it is leader
// ApplyMsg
//   each time a new entry is committed to the log, each Raft peer
//   should send an ApplyMsg to the service (or tester)
//   in the same server.
//

import (
	"math/rand"
	"sync"
	"time"
)
import "lab1/src/labrpc"

// import "bytes"
// import "labgob"



//
// as each Raft peer becomes aware that successive log entries are
// committed, the peer should send an ApplyMsg to the service (or
// tester) on the same server, via the applyCh passed to Make(). set
// CommandValid to true to indicate that the ApplyMsg contains a newly
// committed log entry.
//
// in Lab 3 you'll want to send other kinds of messages (e.g.,
// snapshots) on the applyCh; at that point you can add fields to
// ApplyMsg, but set CommandValid to false for these other uses.
//
type ApplyMsg struct {
	CommandValid bool
	Command      interface{}
	CommandIndex int
}

type LogEntry struct {
	Command interface{}
	Index int
	Term int
}


const (
	// time.Duration() use nanosecond
	HeartbeatInterval    = time.Duration(120) * time.Millisecond
	ElectionTimeoutLower = time.Duration(300) * time.Millisecond
	ElectionTimeoutUpper = time.Duration(400) * time.Millisecond
)

const (
	FOLLOWER  = iota
	CANDIDATE
	LEADER
)

// Prepare for state to use printf

// Prepare for raft to use printf
//func (rf *Raft) String() string {
//	return fmt.Sprintf("[node(%d), state(%v), term(%d), votedFor(%d)]",
//		rf.me, rf.state, rf.currentTerm, rf.votedFor)
//}

// Get the random time between lower and upper
func randTimeDuration(lower, upper time.Duration) time.Duration {
	num := rand.Int63n(upper.Nanoseconds()-lower.Nanoseconds()) + lower.Nanoseconds()
	return time.Duration(num) * time.Nanosecond
}

//
// A Go object implementing a single Raft peer.
//
type Raft struct {
	mu        sync.Mutex          // Lock to protect shared access to this peer's state
	peers     []*labrpc.ClientEnd // RPC end points of all peers
	persister *Persister          // Object to hold this peer's persisted state
	me        int                 // this peer's index into peers[]

	// Your data here (2A, 2B, 2C).
	// Look at the paper's Figure 2 for a description of what
	// state a Raft server must maintain.

	currentTerm    int
	votedFor       int
	electionTimer  *time.Timer // election timeout
	heartbeatTimer *time.Timer // heartbeat timeout
	state          int   // each node state
	voteNum int
}

// return currentTerm and whether this server
// believes it is the leader.
func (rf *Raft) GetState() (int, bool) {

	rf.mu.Lock()
	defer rf.mu.Unlock()

	var term int
	var isleader bool
	// Your code here (2A).
	term = rf.currentTerm
	isleader = rf.state == LEADER
	return term, isleader
}



//
// save Raft's persistent state to stable storage,
// where it can later be retrieved after a crash and restart.
// see paper's Figure 2 for a description of what should be persistent.
//
func (rf *Raft) persist() {
	// Your code here (2C).
	// Example:
	// w := new(bytes.Buffer)
	// e := labgob.NewEncoder(w)
	// e.Encode(rf.xxx)
	// e.Encode(rf.yyy)
	// data := w.Bytes()
	// rf.persister.SaveRaftState(data)

}


//
// restore previously persisted state.
//
func (rf *Raft) readPersist(data []byte) {
	if data == nil || len(data) < 1 { // bootstrap without any state?
		return
	}
	// Your code here (2C).
	// Example:
	// r := bytes.NewBuffer(data)
	// d := labgob.NewDecoder(r)
	// var xxx
	// var yyy
	// if d.Decode(&xxx) != nil ||
	//    d.Decode(&yyy) != nil {
	//   error...
	// } else {
	//   rf.xxx = xxx
	//   rf.yyy = yyy
	// }
}

//
// example RequestVote RPC arguments structure.
// field names must start with capital letters!
//
type RequestVoteArgs struct {
	// Your data here (2A, 2B).
	//下面的变量是论文中直接要求的
	//Term int 			//candidate的term
	//CandidateId int		//candidate ID
	//LastLogIndex int 	//candidate中最后一个log entry的index
	//LastLogTerm int		//candidate中最后一个log entry的term

	Term        int // 2A
	CandidateId int // 2A
}

//
// example RequestVote RPC reply structure.
// field names must start with capital letters!
//
type RequestVoteReply struct {
	// Your data here (2A).
	//Term int 			//follower的term,用于返回candidate来更新它自己的term
	//VoteGranted bool 	//是否可以投票给candidate

	Term        int  // 2A
	VoteGranted bool // 2A if the candidate get this vote
}

type AppendEntryArgs struct {
	//下面几个变量是论文中直接提到的
	Term int
	LeaderId int
	PrevLogIndex int
	PrevLogTerm int
	Entries []LogEntry
	LeaderCommit int
}

type AppendEntryReply struct {
	Term int
	Success bool
}

// 和示例代码相类似,sendAppendEntries是被leader来调用的RPC,意思是向日志
// 中追加log entry
func (rf *Raft) sendAppendEntries (server int, args *AppendEntryArgs,reply *AppendEntryReply) bool{
	ok := rf.peers[server].Call("Raft.AppendEntries", args, reply)
	return ok
}
// AppendEntries RPC handler.
func (rf *Raft) AppendEntries(args *AppendEntryArgs, reply *AppendEntryReply) {
	rf.mu.Lock()
	defer rf.mu.Unlock()
	// if the heartbeat term is smaller
	if args.Term < rf.currentTerm {
		reply.Term = rf.currentTerm
		reply.Success = false
		return
	}

	reply.Success = true
	rf.currentTerm = args.Term
	rf.electionTimer.Reset(randTimeDuration(ElectionTimeoutLower, ElectionTimeoutUpper))
	rf.changeState(FOLLOWER)
	return
}

// example RequestVote RPC handler.
//
func (rf *Raft) RequestVote(args *RequestVoteArgs, reply *RequestVoteReply) {
	// Your code here (2A, 2B).

	rf.mu.Lock()
	defer rf.mu.Unlock()
	if args.Term < rf.currentTerm || (args.Term == rf.currentTerm && rf.votedFor != -1) {
		reply.Term = rf.currentTerm
		reply.VoteGranted = false
		return
	}

	rf.votedFor = args.CandidateId
	rf.currentTerm = args.Term
	reply.VoteGranted = true

	rf.electionTimer.Reset(randTimeDuration(ElectionTimeoutLower, ElectionTimeoutUpper))
	//rf.changeState(FOLLOWER)
}
// convert raft peer's state and deal with the condition of each state
func (rf *Raft) changeState(s int) {
	if s == rf.state {
		return
	}
	_, _ = DPrintf("Term %d: Server %d convert from %v to %v\n", rf.currentTerm, rf.me, rf.state, s)
	rf.state = s
	switch s {
	case FOLLOWER:
		rf.heartbeatTimer.Stop()
		rf.votedFor = -1
	case CANDIDATE:
		rf.startElection()
	case LEADER:
		rf.electionTimer.Stop()
		rf.broadcastHeartbeat()
		rf.heartbeatTimer.Reset(HeartbeatInterval)
	}
}


//
// example code to send a RequestVote RPC to a server.
// server is the index of the target server in rf.peers[].
// expects RPC arguments in args.
// fills in *reply with RPC reply, so caller should
// pass &reply.
// the types of the args and reply passed to Call() must be
// the same as the types of the arguments declared in the
// handler function (including whether they are pointers).
//
// The labrpc package simulates a lossy network, in which servers
// may be unreachable, and in which requests and replies may be lost.
// Call() sends a request and waits for a reply. If a reply arrives
// within a timeout interval, Call() returns true; otherwise
// Call() returns false. Thus Call() may not return for a while.
// A false return can be caused by a dead server, a live server that
// can't be reached, a lost request, or a lost reply.
//
// Call() is guaranteed to return (perhaps after a delay) *except* if the
// handler function on the server side does not return.  Thus there
// is no need to implement your own timeouts around Call().
//
// look at the comments in ../labrpc/labrpc.go for more details.
//
// if you're having trouble getting RPC to work, check that you've
// capitalized all field names in structs passed over RPC, and
// that the caller passes the address of the reply struct with &, not
// the struct itself.
//
func (rf *Raft) sendRequestVote(server int, args *RequestVoteArgs, reply *RequestVoteReply) bool {
	ok := rf.peers[server].Call("Raft.RequestVote", args, reply)
	return ok
}

// start the election in two conditions that
// one is heartbeat timeout that follower convert to the candidate
// another is the candidate elects timeout and restart a new election.
func (rf *Raft) startElection() {
	rf.electionTimer.Reset(randTimeDuration(ElectionTimeoutLower, ElectionTimeoutUpper))
	rf.currentTerm ++
	rf.votedFor = rf.me
	rf.voteNum = 1
	args := RequestVoteArgs{
		Term: rf.currentTerm,
		CandidateId: rf.me,
	}
	var reply RequestVoteReply
	majority := len(rf.peers) / 2 + 1
	for index,_ := range rf.peers {
		if index == rf.me {
			continue
		}
		go func(server int, args *RequestVoteArgs, reply *RequestVoteReply) {
			ok := rf.sendRequestVote(server, args, reply)
			if ok {
				rf.mu.Lock()
				if reply.VoteGranted {
					rf.voteNum ++
					if  rf.voteNum >= majority{
						rf.changeState(LEADER)
					}
				} else {
					if reply.Term > rf.currentTerm {
						rf.currentTerm = reply.Term
						rf.changeState(FOLLOWER)
					}
				}
				rf.mu.Unlock()
			} else {
				_, _ = DPrintf("%v send RequestVote RPC to %v failed", rf, server)
			}
		}(index, &args, &reply)
	}
}

func (rf *Raft) broadcastHeartbeat() {
	args := AppendEntryArgs{
		Term: rf.currentTerm,
		LeaderId: rf.me,
	}
	var reply AppendEntryReply
	for index,_ := range rf.peers {
		if index == rf.me {
			continue
		}
		go func(serverId int, args *AppendEntryArgs, reply *AppendEntryReply) {
			ok := rf.sendAppendEntries(serverId, args, reply)
			if ok {
				rf.mu.Lock()
				if reply.Term > rf.currentTerm {
					rf.currentTerm = reply.Term
					rf.changeState(FOLLOWER)
				}
				rf.mu.Unlock()
			} else {
				_, _ = DPrintf("%v send the heartbeat to %d failed", rf, serverId)
			}
		}(index,&args,&reply)
	}
}

//
// the service using Raft (e.g. a k/v server) wants to start
// agreement on the next command to be appended to Raft's log. if this
// server isn't the leader, returns false. otherwise start the
// agreement and return immediately. there is no guarantee that this
// command will ever be committed to the Raft log, since the leader
// may fail or lose an election. even if the Raft instance has been killed,
// this function should return gracefully.
//
// the first return value is the index that the command will appear at
// if it's ever committed. the second return value is the current
// term. the third return value is true if this server believes it is
// the leader.
//
func (rf *Raft) Start(command interface{}) (int, int, bool) {
	index := -1
	term := -1
	isLeader := true

	// Your code here (2B).


	return index, term, isLeader
}

//
// the tester calls Kill() when a Raft instance won't
// be needed again. you are not required to do anything
// in Kill(), but it might be convenient to (for example)
// turn off debug output from this instance.
//
func (rf *Raft) Kill() {
	// Your code here, if desired.
}


//func (rf *Raft) handleVoteResult(reply *RequestVoteReply) {
//
//}
//
// the service or tester wants to create a Raft server. the ports
// of all the Raft servers (including this one) are in peers[]. this
// server's port is peers[me]. all the servers' peers[] arrays
// have the same order. persister is a place for this server to
// save its persistent state, and also initially holds the most
// recent saved state, if any. applyCh is a channel on which the
// tester or service expects Raft to send ApplyMsg messages.
// Make() must return quickly, so it should start goroutines
// for any long-running work.
//
func Make(peers []*labrpc.ClientEnd, me int,
	persister *Persister, applyCh chan ApplyMsg) *Raft {
	rf := &Raft{}
	rf.peers = peers
	rf.persister = persister
	rf.me = me

	// Your initialization code here (2A, 2B, 2C).
	rf.currentTerm = 0 // initialization is 0
	rf.votedFor = -1   // not to vote
	rf.heartbeatTimer = time.NewTimer(HeartbeatInterval)
	rf.electionTimer = time.NewTimer(randTimeDuration(ElectionTimeoutLower, ElectionTimeoutUpper))
	rf.state = FOLLOWER // init is follower

	// deal with the things about the timer
	go func(node *Raft) {
		for {
			select {
			case <-rf.electionTimer.C:
				if rf.state == FOLLOWER {
					rf.changeState(CANDIDATE)
				} else {
					rf.startElection()
				}
			case <-rf.heartbeatTimer.C:
				if rf.state == LEADER {
					rf.broadcastHeartbeat()
					rf.heartbeatTimer.Reset(HeartbeatInterval)
				}
			}
		}
	}(rf)

	// initialize from state persisted before a crash
	rf.readPersist(persister.ReadRaftState())

	return rf
}

