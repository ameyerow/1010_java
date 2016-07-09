package meyerowitz.alpha;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class Game extends JPanel
{	
	private static final long serialVersionUID = -6280861151097230288L;
	
	Tile[][] tiles;
	
	public Game()
	{
		tiles = new Tile[10][10];	
	}
	
	public static void main(String[] args)
	{
		JFrame game = new JFrame();
		game.setTitle("1010!");
		game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		game.setSize(340, 400);
		game.setResizable(false);
		game.add(new Game());
		game.setLocationRelativeTo(null);
		game.setVisible(true); 
	}
}
