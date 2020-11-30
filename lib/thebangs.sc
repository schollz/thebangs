// "the" bangs

Thebangs  {
	classvar maxVoices = 32;

	var server;
	var group;

	// synth params
	var <>hz1;
	var <>hz2;
	var <>mod1;
	var <>mod2;
	var <>amp;
	var <>pan;
	var <>attack;
	var <>release;

	// some bangs
	var bangs;
	// the bang - a method of Bangs, as a string
	var <thebang;
	// which bang - numerical index
	var <whichbang;

	var <voicer;

	*new { arg srv;
		^super.new.init(srv);
	}

	init {
		arg srv;
		server = srv;
		group = Group.new(server);

		// default parameter values
		hz1 = 330;
		hz2 = 10000;
		mod1 = 0.5;
		mod2 = 0.0;

		attack = 0.01;
		release = 2;
		amp = 0.1;
		pan = 0.0;

		bangs = Bangs.class.methods.collect({|m| m.name});
		bangs.do({|name| postln(name); });

		this.whichBang = 0;

		voicer = OneshotVoicer.new(maxVoices);
	}

	bang_{ arg name;
		thebang = name;
	}

	whichBang_ { arg i;
		whichbang = i;
		thebang = bangs[whichbang];
	}

	bang { arg hz;
		var fn, gr, srv;
		postln("bang!");

		if (hz != nil, { hz1 = hz; });

		srv = server;
		gr = group;
		
		fn = {
			var syn;
			syn = {
				arg gate=1;
				var snd, perc, ender;

				perc = EnvGen.ar(Env.perc(attack, release), doneAction:Done.freeSelf);
				ender = EnvGen.ar(Env.asr(0, 1, 0.01), gate:gate, doneAction:Done.freeSelf);

				snd = Bangs.perform(thebang, hz1, mod1, hz2, mod2, perc);

				Out.ar(0, Pan2.ar(snd * perc * amp * ender, pan));

			}.play(srv);
			syn
		};

		voicer.newVoice(fn);
	}


	freeAllNotes {
		// do need this to keep the voicer in sync..
		voicer.stopAllVoices;
		group.set(\gate, 0);
	}

	free {
		group.set(\gate, 0);
	}

	
}