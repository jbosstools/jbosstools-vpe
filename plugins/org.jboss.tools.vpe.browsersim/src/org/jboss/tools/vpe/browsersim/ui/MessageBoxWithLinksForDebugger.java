package org.jboss.tools.vpe.browsersim.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class MessageBoxWithLinksForDebugger extends CustomMessageBox {
	private String message;
	private String url;
	private String header;

	public MessageBoxWithLinksForDebugger(Shell parent, String message, String url, Image icon, String header) {
		super(parent, icon);
		this.message = message;
		this.url = url;
		this.header = header;
	}

	@Override
	protected void createWidgets() {
		super.createWidgets();
		Link link = new Link(getMessageComposite(), SWT.WRAP);
		link.setText(message);
		link.setBackground(getMessageCompositeBackground());
		link.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				Program.launch(event.text);
			}
		});

		Composite buttonsComposite = new Composite(getButtonsComposite(), SWT.NONE);
		GridData gridData = new GridData(SWT.END, SWT.FILL, true, true);
		buttonsComposite.setLayoutData(gridData);
		GridLayout buttonsCompositeLayout = new GridLayout(2, false);
		buttonsCompositeLayout.marginHeight = 10;
		buttonsCompositeLayout.marginWidth = 10;
		buttonsComposite.setLayout(buttonsCompositeLayout);

		Button copyLink = new Button(buttonsComposite, SWT.PUSH);
		Button ok = new Button(buttonsComposite, SWT.PUSH);

		copyLink.setText(Messages.ExceptionNotifier_COPY_LINK);
		copyLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Display display = Display.getDefault();
				Clipboard clipboard = new Clipboard(display);
				TextTransfer textTransfer = TextTransfer.getInstance();
				clipboard.setContents(new Object[] { url }, new Transfer[] { textTransfer });
				clipboard.dispose();
			}
		});

		ok.setText(Messages.ExceptionNotifier_OK);
		ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getShell().close();
			}
		});

		GridData copyLinkLayoutData = new GridData(SWT.END, SWT.CENTER, true, true);
		copyLinkLayoutData.widthHint = 88;
		copyLink.setLayoutData(copyLinkLayoutData);

		GridData okLayoutData = new GridData(SWT.END, SWT.CENTER, true, true);
		okLayoutData.widthHint = 88;
		ok.setLayoutData(okLayoutData);

		getShell().setText(header);
		getShell().setDefaultButton(ok);
		getShell().pack();
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setHeader(String header) {
		this.header = header;
	}

}
