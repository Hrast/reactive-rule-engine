/**
 * Reactive rule engine
 *
 * Copyright (c) 2015, UniCredit Business Integrated Solutions S.c.p.A
 *
 * This project includes software developed by UniCredit Business Integrated Solutions S.c.p.A
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package eu.unicredit.dna.rre;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.kie.api.KieServices;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * */
public class RulesProcessor extends UntypedActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private final KieSession kSession;
	private final ExecutorService ex;

	public RulesProcessor(ActorRef destination, String kieSessionName) {
		final KieServices ks = KieServices.Factory.get();
		final KieContainer kc = ks.getKieClasspathContainer();
		kSession = kc.newKieSession(kieSessionName);
		kSession.addEventListener(new ReactionEventListener(destination));
		ex = Executors.newSingleThreadExecutor();
		ex.submit((Runnable) kSession::fireUntilHalt);
	}

	public static Props props(ActorRef publisher, String kieSessionName) {
		return Props.create(RulesProcessor.class, publisher, kieSessionName);
	}

	@Override
	public void onReceive(Object message) throws Exception {
		log.debug("Received {}", message.getClass().getSimpleName());
		if (message instanceof DroolsMessage) {
			kSession.insert(message);
		} else {
			unhandled(message);
		}
	}

	@Override
	public void postStop() throws Exception {
		kSession.halt();
		ex.shutdown();
		kSession.dispose();
	}

	private class ReactionEventListener implements RuleRuntimeEventListener {

		private final ActorRef destination;

		private ReactionEventListener(ActorRef destination) {
			this.destination = destination;
		}

		@Override
		public void objectInserted(final ObjectInsertedEvent event) {
			final Reaction reaction = (Reaction) event.getObject();
			log.info("Generated object {}", reaction);
			destination.tell(reaction, getSelf());
			kSession.delete(event.getFactHandle());
		}

		@Override
		public void objectUpdated(final ObjectUpdatedEvent event) {

		}

		@Override
		public void objectDeleted(final ObjectDeletedEvent event) {

		}
	}
}