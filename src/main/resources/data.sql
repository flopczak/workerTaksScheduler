INSERT INTO workers(name) VALUES(1);
INSERT INTO workers(name) VALUES(2);
INSERT INTO workers(name) VALUES(3);
INSERT INTO workers(name) VALUES(4);
INSERT INTO workers(name) VALUES(5);


INSERT INTO processes(PROCESS_ID) VALUES(1);
INSERT INTO processes(PROCESS_ID) VALUES(2);
INSERT INTO processes(PROCESS_ID) VALUES(3);
INSERT INTO processes(PROCESS_ID) VALUES(4);

INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(1,1,1);
INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(2,2,1);
INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(3,3,1);
INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(4,4,1);
INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(5,5,1);
INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(6,1,2);
INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(7,2,2);
INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(8,3,2);
INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(9,4,2);
INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(10,5,2);
INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(11,1,3);
INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(12,2,3);
INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(13,3,3);
INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(14,4,3);
INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(15,5,3);
INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(16,1,4);
INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(17,2,4);
INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(18,3,4);
INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(19,4,4);
INSERT INTO tasks(TASK_ID, type, PROCESS_ID) VALUES(20,5,4);




INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(1,1,70);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(1,2,144);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(1,3,500);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(1,4,120);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(1,5,300);

INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(2,1,4444);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(2,2,6000);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(2,3,2800);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(2,4,3000);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(2,5,8000);

INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(3,1,7000);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(3,2,5000);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(3,3,4225);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(3,4,3222);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(3,5,4000);

INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(4,1,660);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(4,2,450);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(4,3,365);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(4,4,300);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(4,5,120);

INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(5,1,12000);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(5,2,13000);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(5,3,10111);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(5,4,20000);
INSERT INTO statistics(task_type,worker_name,estimated_time_in_seconds) VALUES(5,5,15121);

