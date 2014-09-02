--- Some test values for use the DBQueryTest

--- This is horribly out of date

INSERT INTO person (person_id,handle,person_name,location,verified,url,created) VALUES (434344,'fancypants','Dr Steven Pants','Chicago, IL, USA',FALSE,'http://twitter.com/fancypants','2010-05-12');
INSERT INTO person (person_id,handle,person_name,location,verified,url,created) VALUES (1884,'potatofarmer12','Johnny Potatoseed','Idaho Falls, ID, USA',FALSE,'http://twitter.com/potatofarmer12','2011-06-20');
INSERT INTO person (person_id,handle,person_name,location,verified,url,created) VALUES (3498984,'crayons34','Billy Johnson','Tokyo, Japan',TRUE,'http://twitter.com/crayons34','2008-06-20');

INSERT INTO followers (person_id,follower) VALUES (434344,1884);
INSERT INTO followers (person_id,follower) VALUES (434344,3498984);
INSERT INTO followers (person_id,follower) VALUES (1884,3498984);

INSERT INTO tweet (tweet_id,person_id,tweet) VALUES (1200,434344,'LOL!');
INSERT INTO tweet (tweet_id,person_id,tweet) VALUES (1201,434344,'HIYA GUYS');
INSERT INTO tweet (tweet_id,person_id,tweet) VALUES (1202,434344,'I once knew a man who ate potatos');
INSERT INTO tweet (tweet_id,person_id,tweet) VALUES (1203,434344,'Fishy!');

INSERT INTO tweet (tweet_id,person_id,tweet,reply_to_user,reply_to_status) VALUES (1204,1884,'Fish is really good right?',434344,1203);
