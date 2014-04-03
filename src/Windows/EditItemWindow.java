package Windows;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import Game_System.Item;
import Panels.ItemManagerPanel;
import UserIO.WindowComm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class EditItemWindow extends JFrame {

	private JLabel 				lblItemTitleLg;
	private JTextField 			m_NameTextArea;
	private JTextArea 			m_DescTextArea;
	private JTextField 			m_DropSceneLbl;
	private JTextField 			m_UnlockSceneLbl;
	private WindowComm 			m_CommWindow;
	private ItemManagerPanel 	m_ItmMngrParent;
	private Item				m_EditingItem;
	private JButton 			m_CancelBtn;
	private JButton 			m_SaveItemBtn;
	private boolean				m_bNewItem;
	
	// Constant variables
	private static final int YES 	= 0;
	private static final int NO 	= 1;

	
	public class ItemButtonHandler implements ActionListener {
		private EditItemWindow window;

		public ItemButtonHandler(EditItemWindow window) {
			this.window = window;
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if( e.getSource( ).equals( m_SaveItemBtn ) )
				saveItem( );
			else if( e.getSource( ).equals( m_CancelBtn ) )
				cancel( );
		}
	}
	
	public class ItemMouseHandler implements MouseListener
	{
		private EditItemWindow m_Window;
		
		public ItemMouseHandler( EditItemWindow parentWindow )
		{
			this.m_Window = parentWindow;
		}

		@Override
        public void mousePressed( MouseEvent e )	
		{
			if( e.getSource( ).equals( m_DropSceneLbl ) )
				m_CommWindow.displayMessage( "Drop Scene Lable Clicked!" );
			else if( e.getSource( ).equals( m_UnlockSceneLbl ) )
				m_CommWindow.displayMessage( "Unlock Scene Lable Clicked!" );
		}

		/* Unimplemented, but inherited events as required by MouseListener children. */
		@Override
        public void mouseClicked( MouseEvent arg0 )	{ }

		@Override
        public void mouseEntered( MouseEvent e ) 	{ }

		@Override
        public void mouseExited( MouseEvent e )		{ }

		@Override
        public void mouseReleased( MouseEvent e )	{ }
		
	}
	
	/****************************************************************\
	 * Edit Item Window Function Block								*
	\****************************************************************/
	
	/**
	 * Sets the Item values and attempts to save the item through the parent.
	 * If it can be saved, then it's saved and the window closes.
	 * If not, the user can confirm if they want to exit without saving or not
	 * and the program handles it accordingly.
	 */
	private void saveItem( )
	{		
		m_EditingItem.setName( m_NameTextArea.getText( ) );
		m_EditingItem.setDesc( m_DescTextArea.getText( ) );
		
		if( !m_bNewItem || m_ItmMngrParent.canAddItem( m_EditingItem ) )
		{
			m_ItmMngrParent.saveItem( m_EditingItem );
			closeWindow( );
		}
		else
		{
			int iResult = m_CommWindow.getYesNo( "An Item with the name \"" + 
												 m_EditingItem.getName( ) +
												 "\" already exists, exit anyway without saving?", 
												 "Can't save Item!" );
			
			if( iResult == YES )
				closeWindow( );
		}
	}
	
	/**
	 * Checks to see if any changes were made to the item.  If there are changes, then
	 * prompt the user to see if they'd like to save the changes and handle accordingly.
	 */
	private void cancel( )
	{
		boolean bChanged = !m_EditingItem.getName( ).equals( m_NameTextArea.getText( ) ) && !m_bNewItem;
		bChanged |= !m_EditingItem.getDesc( ).equals( m_DescTextArea.getText( ) );
		
		if( bChanged )
		{
			int iResult = m_CommWindow.getYesNo( "Would you like to save your changes?", "Save" );
			
			if( iResult == YES )
				saveItem( );
		}
		
		closeWindow( );
	}
	
	private void closeWindow()
	{
		m_ItmMngrParent.editItemWindowHasClosed();
		dispose( );
	}
	
	/****************************************************************\
	 * End Edit Item Window Function Block							*
	\****************************************************************/
	/**
	 * Create the application.
	 */
	public EditItemWindow( Item itemToEdit, ItemManagerPanel parentWindow, boolean bNewItem )
	{
		getContentPane().setLayout(null);
		m_ItmMngrParent = parentWindow;
		m_EditingItem 	= itemToEdit;
		m_bNewItem		= bNewItem;
		
		/* Init some local variables */
		m_CommWindow = new WindowComm( this );
		ItemButtonHandler m_ItmBtnHndlr = new ItemButtonHandler( this );
		ItemMouseHandler m_ItmMouseHndlr = new ItemMouseHandler( this );
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) { cancel(); }
		});
		
		/* set up location of Window */
		setBounds( 100, 100, 594, 440 );
		Point parentLocation = m_ItmMngrParent.getLocation();
		double parentX = parentLocation.getX();
		double parentY = parentLocation.getY();
		
		setLocation((int)(parentX + 125),(int)(parentY + 125));
		
		lblItemTitleLg = new JLabel( m_EditingItem.getName( ) );
		lblItemTitleLg.setBounds(10, 0, 558, 31);
		lblItemTitleLg.setHorizontalAlignment(SwingConstants.CENTER);
		lblItemTitleLg.setFont(new Font("Tahoma", Font.PLAIN, 20));
		getContentPane().add(lblItemTitleLg);
		
		m_NameTextArea = new JTextField( m_EditingItem.getName( ) );
		m_NameTextArea.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		m_NameTextArea.setBounds(10, 72, 193, 20);
		getContentPane().add(m_NameTextArea);
		m_NameTextArea.setColumns(10);
		
		JLabel lblItemName = new JLabel( "Item Name:" );
		lblItemName.setBounds(10, 47, 63, 14);
		getContentPane().add(lblItemName);
		
		m_DescTextArea = new JTextArea( m_EditingItem.getDesc( ) );
		m_DescTextArea.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		m_DescTextArea.setLineWrap(true);
		m_DescTextArea.setBounds(10, 148, 299, 225);
		getContentPane().add(m_DescTextArea);
		
		JLabel lblItemDescription = new JLabel("Item Description:");
		lblItemDescription.setBounds(10, 123, 110, 14);
		getContentPane().add(lblItemDescription);
		
		JLabel sceneDroppedLbl = new JLabel("Item Dropped in:");
		sceneDroppedLbl.setBounds(348, 42, 199, 14);
		getContentPane().add(sceneDroppedLbl);
		
		m_DropSceneLbl = new JTextField();
		m_DropSceneLbl.addMouseListener( m_ItmMouseHndlr );
		m_DropSceneLbl.setEditable(false);
		m_DropSceneLbl.setBounds(348, 67, 199, 20);
		getContentPane().add(m_DropSceneLbl);
		m_DropSceneLbl.setColumns(10);
		
		JLabel sceneUnLockTitleLbl = new JLabel("Item Unlocks:");
		sceneUnLockTitleLbl.setBounds(348, 98, 199, 14);
		getContentPane().add(sceneUnLockTitleLbl);
		
		m_UnlockSceneLbl = new JTextField();
		m_UnlockSceneLbl.addMouseListener( m_ItmMouseHndlr );
		m_UnlockSceneLbl.setEditable(false);
		m_UnlockSceneLbl.setColumns(10);
		m_UnlockSceneLbl.setBounds(348, 123, 199, 20);
		getContentPane().add(m_UnlockSceneLbl);
		
		/* Initialize Buttons */
		
		m_SaveItemBtn = new JButton("Save Item");
		m_SaveItemBtn.addActionListener( m_ItmBtnHndlr );
		m_SaveItemBtn.setBounds(348, 185, 110, 23);
		getContentPane().add(m_SaveItemBtn);
		
		m_CancelBtn = new JButton("Cancel");
		m_CancelBtn.addActionListener( m_ItmBtnHndlr );
		m_CancelBtn.setBounds(348, 219, 110, 23);
		getContentPane().add(m_CancelBtn);
	}
	
	public void run()
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					setVisible(true);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}
}
